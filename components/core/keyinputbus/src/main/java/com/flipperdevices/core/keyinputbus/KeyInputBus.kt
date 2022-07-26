package com.flipperdevices.core.keyinputbus

import android.view.KeyEvent
import androidx.lifecycle.LifecycleOwner

/**
 * We have no way to intercept character input in a particular place inside Compose.
 * So we use ComposeView and the global EventBus.
 *
 * More:
 * To be fair, we have ways of catching the delete button:
 * 1) Create a custom ComposeView and override dispatchKeyEvent (this way)
 * 2) Use my LocalTextInputService and create an input connection there
 * 3) Use an invisible character from https://invisible-characters.com/
 * 4) Move cursor from 0 position to previous input: [00][|00] -> [00|][00]
 *
 * But all methods have disadvantages. Either for the user or for the architecture.
 * We chose the dirtiest way for the architecture, but which works best and most stable.
 *
 * There may still be good ways to do this in the future:
 * https://twitter.com/ychescale9/status/1372216269542334464
 */
interface KeyInputBus {
    fun subscribe(lifecycleOwner: LifecycleOwner, keyInputBusListener: KeyInputBusListener)
    fun onKeyPress(keyEvent: KeyEvent)
}
