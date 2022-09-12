package com.flipperdevices.wearable.emulate.handheld.impl.service

import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearServiceComponent
import com.google.android.gms.wearable.MessageEvent

class WearRequestListenerService : LifecycleWearableListenerService(), LogTagProvider {
    override val TAG = "WearRequestListenerService"

    init {
        ComponentHolder.component<WearServiceComponent>().inject(this)
    }

    override fun onMessageReceived(message: MessageEvent) {
        super.onMessageReceived(message)
        info { "Receive message $message" }
    }
}
