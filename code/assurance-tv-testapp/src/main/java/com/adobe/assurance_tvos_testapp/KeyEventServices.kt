package com.adobe.assurance_tvos_testapp

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class KeyEventServices: AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        TODO("Not yet implemented")
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
    override fun onKeyEvent(event: KeyEvent?): Boolean {
        event?.let {
            val keyCode = it.keyCode
            val action = if (it.action == KeyEvent.ACTION_DOWN) "pressed" else "released"
            Log.d("XXXXX", "Direct key event - Key code: $keyCode, Action: $action")
        }
        // Return true if you've consumed the event, false otherwise
        return super.onKeyEvent(event)
    }
}