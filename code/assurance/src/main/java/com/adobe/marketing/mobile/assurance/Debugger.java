package com.adobe.marketing.mobile.assurance;

import com.adobe.marketing.mobile.LogDataHandler;
import com.adobe.marketing.mobile.services.Logging;
import com.adobe.marketing.mobile.services.ServiceProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Debugger {

  private static final AtomicReference<LogDataHandler> HANDLER = new AtomicReference<>(null);

  static void registerLogForwardingHandler(LogDataHandler handler) {
    HANDLER.set(handler);
  }

  private static final List<Map<String, Object>> cachedLogs = new ArrayList<>();

  static void forwardLogData(Map<String, Object> data) {
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

  public static void queueLogsWithMetaData() {
    ServiceProvider.getInstance().setLoggingService(new Logging() {
      private static final String TAG = "AdobeExperienceSDK";

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
    });
  }
}
