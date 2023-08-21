package com.flipperdevices.wearable.emulate.api

import kotlinx.coroutines.CoroutineScope

interface ChannelClientHelper {
    fun onChannelOpen(scope: CoroutineScope)

    fun onChannelCloseFromPhone(scope: CoroutineScope)

    fun onDestroy()
}
