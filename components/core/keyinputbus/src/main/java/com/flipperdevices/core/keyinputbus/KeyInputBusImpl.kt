package com.flipperdevices.core.keyinputbus

import android.view.KeyEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.subscribeOnFirst
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class)
class KeyInputBusImpl @Inject constructor() : KeyInputBus {
    private val listeners = ArrayList<KeyInputBusListener>()

    @Synchronized
    override fun subscribe(
        lifecycleOwner: LifecycleOwner,
        keyInputBusListener: KeyInputBusListener
    ) {
        listeners.add(keyInputBusListener)
        lifecycleOwner.subscribeOnFirst(Lifecycle.Event.ON_DESTROY) {
            listeners.remove(keyInputBusListener)
        }
    }

    @Synchronized
    override fun onKeyPress(keyEvent: KeyEvent) {
        listeners.forEach {
            it.onKeyEvent(keyEvent)
        }
    }
}
