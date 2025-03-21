package com.adobe.marketing.mobile

import android.view.KeyEvent
enum class KeyType {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    companion object {
        fun fromKeyCode(code: Int): KeyType? {
            return when (code) {
                KeyEvent.KEYCODE_DPAD_UP -> UP
                KeyEvent.KEYCODE_DPAD_DOWN -> DOWN
                KeyEvent.KEYCODE_DPAD_LEFT -> LEFT
                KeyEvent.KEYCODE_DPAD_RIGHT -> RIGHT
                else -> null
            }
        }
    }
}