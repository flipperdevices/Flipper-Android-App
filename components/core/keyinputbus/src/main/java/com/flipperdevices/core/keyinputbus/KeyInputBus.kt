package com.flipperdevices.core.keyinputbus

import android.view.KeyEvent
import androidx.lifecycle.LifecycleOwner

/**
 * We have no way to intercept character input in a particular place inside Compose.
 * So we use ComposeView and the global EventBus.
 */
interface KeyInputBus {
    fun subscribe(lifecycleOwner: LifecycleOwner, keyInputBusListener: KeyInputBusListener)
    fun onKeyPress(keyEvent: KeyEvent)
}
