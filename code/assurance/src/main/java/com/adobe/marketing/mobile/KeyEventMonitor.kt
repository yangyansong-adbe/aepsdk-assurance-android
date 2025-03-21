package com.adobe.marketing.mobile

import android.view.KeyEvent


class KeyEventMonitor internal constructor(private val keyCombination: Array<KeyType>) {

    private val cachedKeys = mutableListOf<KeyType>()
    private var isConnected = false

    fun keyEventDetected(event: KeyEvent) {
        if (event.action == KeyEvent.ACTION_DOWN) return
        KeyType.fromKeyCode(event.keyCode)?.let { keyType ->
            handleKeyType(keyType)
        } ?: cachedKeys.clear()
    }

    private fun handleKeyType(keyType: KeyType) {
        cachedKeys.apply {
            if (!isValidSequence() || size >= keyCombination.size) {
                clear()
            }
            add(keyType)

            if (size == keyCombination.size && isValidSequence()) {
                clear()
                keyCombinationDetected()
            }
        }
    }

    private fun isValidSequence(): Boolean =
        cachedKeys.zip(keyCombination)
            .take(cachedKeys.size)
            .all { (cached, expected) -> cached == expected }

    private fun keyCombinationDetected() {
        if (isConnected) {
            Assurance.endSession()
        } else {
            Assurance.startSession()
        }
    }

}