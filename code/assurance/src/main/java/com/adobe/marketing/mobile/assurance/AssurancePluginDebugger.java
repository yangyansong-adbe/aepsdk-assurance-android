package com.adobe.marketing.mobile.assurance;

import android.content.Context;
import com.adobe.marketing.mobile.Assurance;
import com.adobe.marketing.mobile.Event;
import com.adobe.marketing.mobile.MobileCore;
import com.adobe.marketing.mobile.services.Log;
import com.adobe.marketing.mobile.services.ServiceProvider;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AssurancePluginDebugger implements AssurancePlugin {

  private static final String LOG_TAG = "AssurancePluginDebugger";

  private final AtomicReference<AssuranceSession> parentSession = new AtomicReference<>(null);

  private boolean logEnabled = false;

  @Override
  public String getVendor() {
    return AssuranceConstants.VENDOR_ASSURANCE_MOBILE;
  }

  @Override
  public String getControlType() {
    return "sdkDebugger";
  }

  private int getEventSubType(AssuranceEvent event) {
    if (event.getPayload().containsKey("subType")) {
      switch (Objects.requireNonNull(event.getPayload().get("subType")).toString()) {
        case "loadTrigger":
          return 0;
        case "command":
          return 1;
        default:
          return -1;
      }
    } else {
      return -1;
    }
  }

  private Map<String, Object> retrieveSharedPreferences() {
    Map<String, Object> result = new HashMap<>();
    Context context = ServiceProvider.getInstance().getAppContextService().getApplicationContext();
    File prefDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
    if (prefDir.exists() && prefDir.isDirectory()) {
      String[] list = prefDir.list();
      for (String file : list) {
        if (file.endsWith(".xml")) {
          String name = file.substring(0, file.length() - 4);
          Log.debug(Assurance.LOG_TAG, LOG_TAG, "Shared Preference: " + name);
          result.put(name, new HashMap<String, Object>());
          Map<String, Object> kv = new HashMap<>();
          context.getSharedPreferences(name, Context.MODE_PRIVATE).getAll().forEach((k, v) -> {
            Log.debug(Assurance.LOG_TAG, LOG_TAG, "Key: " + k + " Value: " + v);
            kv.put(k, v);
          });
          result.put(name, kv);
        }
      }
    }
    return result;
  }

  private void handleCommand(String args) {
    if (args.isEmpty()) {
      return;
    }
    final Map<String, Object> result = new HashMap<>();
    switch (args) {
      case "prefs":
        result.putAll(retrieveSharedPreferences());
        break;
      case "pause":
        MobileCore.dispatchEvent(
            new Event.Builder("Debug Event", "debug", "assurance").setEventData(
                    new HashMap<String, Object>() {
                      {
                        put("action", "pause");
                      }
                    })
                .build());
        return;
      case "resume":
        MobileCore.dispatchEvent(
            new Event.Builder("Debug Event", "debug", "assurance").setEventData(
                    new HashMap<String, Object>() {
                      {
                        put("action", "resume");
                      }
                    })
                .build());
        return;
      default:
        result.put("result", "failure");
        result.put("reason", "Invalid command");
        break;
    }
    Debugger.forwardLogData(new HashMap<String, Object>() {
      {
        put("tag", "CMD");
        put("timestamp", System.currentTimeMillis());
        put("result", result);
        put("metaData", new HashMap<String, Object>());
      }
    });
  }

  @Override
  public void onEventReceived(AssuranceEvent event) {
    final HashMap<String, Object> logForwardingDetails = event.getControlDetail();

    if (AssuranceUtil.isNullOrEmpty(logForwardingDetails)) {
      Log.warning(
          Assurance.LOG_TAG,
          LOG_TAG,
          "Invalid details in payload. Ignoring to enable/disable logs.");
      return;
    }

    int eventSubType = getEventSubType(event);
    if (eventSubType == 1) {
      handleCommand(Objects.requireNonNull(event.getControlDetail().get("args")).toString());
      return;
    }

    final Object started = logForwardingDetails.get("started");
    if (!(started instanceof Boolean)) {
      Log.warning(Assurance.LOG_TAG, LOG_TAG,
          "Unable to forward the log, logForwardingValue is invalid");
      return;
    }

    logEnabled = (Boolean) started;
    final AssuranceSession session = parentSession.get();

    if (logEnabled) {
      if (session != null) {
        session.logLocalUI(AssuranceConstants.UILogColorVisibility.HIGH,
            "Received Assurance command to start forwarding logs");
      }
      Debugger.registerLogForwardingHandler(data -> {
        final AssuranceEvent logEvent = new AssuranceEvent(
            "log", data);
        if (session != null) {
          session.queueOutboundEvent(logEvent);
        }
      });
    } else {
      if (session != null) {
        session.logLocalUI(AssuranceConstants.UILogColorVisibility.HIGH,
            "Received Assurance command to stop forwarding logs");
      }
    }
  }

  @Override
  public void onRegistered(AssuranceSession parentSession) {
    this.parentSession.set(parentSession);
  }

  @Override
  public void onSessionConnected() {

  }

  @Override
  public void onSessionDisconnected(int code) {
    logEnabled = false;
  }

  @Override
  public void onSessionTerminated() {
    parentSession.set(null);
  }
}
