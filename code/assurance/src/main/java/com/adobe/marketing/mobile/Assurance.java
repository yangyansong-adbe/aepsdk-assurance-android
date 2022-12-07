/*
 * Copyright 2022 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.adobe.marketing.mobile;

import com.adobe.marketing.mobile.assurance.AssuranceExtension;
import com.adobe.marketing.mobile.services.Log;
import com.adobe.marketing.mobile.services.Logging;
import com.adobe.marketing.mobile.services.ServiceProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Assurance {

    public static final Class<? extends Extension> EXTENSION = AssuranceExtension.class;
    public static final String LOG_TAG = "Assurance";
    public static final String EXTENSION_VERSION = "2.0.0";
    public static final String EXTENSION_NAME = "com.adobe.assurance";

    private static final String DEEPLINK_SESSION_ID_KEY = "adb_validation_sessionid";
    private static final String START_SESSION_URL = "startSessionURL";

    // ========================================================================================
    // Public APIs
    // ========================================================================================

    /**
     * Returns the current version of the Assurance extension.
     *
     * @return A {@link String} representing Assurance extension version
     */
    public static String extensionVersion() {
        return EXTENSION_VERSION;
    }

    /**
     * Register Assurance extension with {@code MobileCore}
     * <p>
     * This will allow the extension to send and receive events to and from the {@code MobileCore}.
     *
     * @return returns an boolean as a result of the Assurance extension registration.
     */
    @Deprecated
    public static boolean registerExtension() {
        ExtensionErrorCallback<ExtensionError> errorCallback = new ExtensionErrorCallback<ExtensionError>() {
            @Override
            public void error(final ExtensionError adbExtensionError) {
                Log.error(LOG_TAG, LOG_TAG,
                        String.format("Assurance registration failed with error %s. " +
                                        "For more details refer to" +
                                        " https://aep-sdks.gitbook.io/docs/beta/project-griffon/set-up-project-griffon#register-griffon-with-mobile-core",
                                adbExtensionError.getErrorName()));
            }
        };
        return MobileCore.registerExtension(AssuranceExtension.class, errorCallback);
    }

    /**
     * Starts a Project Assurance session with the provided URL
     * <p>
     * Calling this method when a session has already been started will result in a no-op.
     * It will attempt to initiate a new Project Assurance session if no session is active.
     *
     * @param url a valid Project Assurance deeplink URL to start a session
     */
    public static void startSession(final String url) {
        // validate the obtained URL
        if (!url.contains(DEEPLINK_SESSION_ID_KEY)) {
            Log.warning(LOG_TAG, LOG_TAG,
                    String.format("Not a valid Assurance deeplink, Ignorning start session API call. URL : %s", url));
            return;
        }

        final Map<String, Object> startSessionEventData = new HashMap();
        startSessionEventData.put(START_SESSION_URL, url);

        final Event startSessionEvent = new Event.Builder("Assurance Start Session",
                EventType.ASSURANCE,
                EventSource.REQUEST_CONTENT)
                .setEventData(startSessionEventData).build();
        MobileCore.dispatchEvent(startSessionEvent);
    }

    private static AtomicReference<LogDataHandler> HANDLER = new AtomicReference<>(null);

    public static void registerLogForwardingHandler(LogDataHandler handler) {
        HANDLER.set(handler);
    }

    public static void queueLogsWithMetaData() {
        ServiceProvider.getInstance().setLoggingService(new Logging() {
            private static final String TAG = "AdobeExperienceSDK";
            private final List<Map<String, Object>> cachedLogs = new ArrayList<>();

            @Override
            public void trace(final String tag, final String message) {
                this.trace(tag, message, new HashMap<>());
            }

            @Override
            public void trace(String tag, String message, Map<String, Object> metaData) {
                android.util.Log.v(TAG, tag + " - " + message);
                Map<String, Object> data = new HashMap<>();
                data.put("tag", tag);
                data.put("message", message);
                data.put("level", "trace");
                data.put("metaData", metaData);
                forwardLogData(data);
            }

            @Override
            public void debug(final String tag, final String message) {
                this.debug(tag, message, new HashMap<>());
            }

            @Override
            public void debug(String tag, String message, Map<String, Object> metaData) {
                android.util.Log.d(TAG, tag + " - " + message);
                Map<String, Object> data = new HashMap<>();
                data.put("tag", tag);
                data.put("message", message);
                data.put("level", "debug");
                data.put("metaData", metaData);
                forwardLogData(data);
            }

            @Override
            public void warning(final String tag, final String message) {
                this.warning(tag, message, new HashMap<>());
            }

            @Override
            public void warning(String tag, String message, Map<String, Object> metaData) {

                android.util.Log.w(TAG, tag + " - " + message);
                Map<String, Object> data = new HashMap<>();
                data.put("tag", tag);
                data.put("message", message);
                data.put("level", "warning");
                data.put("metaData", metaData);
                forwardLogData(data);
            }

            @Override
            public void error(final String tag, final String message) {
                this.error(tag, message, new HashMap<>());
            }

            @Override
            public void error(String tag, String message, Map<String, Object> metaData) {
                android.util.Log.e(TAG, tag + " - " + message);
                Map<String, Object> data = new HashMap<>();
                data.put("tag", tag);
                data.put("message", message);
                data.put("level", "error");
                data.put("metaData", metaData);
                forwardLogData(data);
            }

            private void forwardLogData(Map<String, Object> data) {
                if (data.get("tag").toString().contains("Assurance")) {
                    return;
                } else {
//                    String extensionName = data.get("tag").toString().split("/")[0];
                    data.put("extension", Thread.currentThread().getName());
                    data.put("timestamp", System.currentTimeMillis());
                    data.put("threadName", Thread.currentThread().getName());
                }
                LogDataHandler handler = HANDLER.get();
                if (handler != null) {
                    if (!cachedLogs.isEmpty()) {
                        for (Map<String, Object> item : cachedLogs) {
                            handler.execute(item);
                        }
                        cachedLogs.clear();
                    }
                    handler.execute(data);
                } else {
                    cachedLogs.add(data);
                }
            }
        });
    }
}
