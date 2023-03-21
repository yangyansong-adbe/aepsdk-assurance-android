package com.adobe.marketing.mobile.assurance;

import com.adobe.marketing.mobile.Assurance;
import com.adobe.marketing.mobile.Debugger;
import com.adobe.marketing.mobile.services.Log;
import java.util.HashMap;
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
            AssuranceConstants.AssuranceEventType.LOG, data);
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
