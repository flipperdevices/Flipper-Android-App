package com.flipperdevices.core.keyinputbus

import android.view.KeyEvent

fun interface KeyInputBusListener {
    fun onKeyEvent(keyEvent: KeyEvent)
}
