package com.flipperdevices.wearable.emulate.api

import com.flipperdevices.wearable.emulate.model.ChannelClientState
import com.google.android.gms.wearable.ChannelClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface ChannelClientHelper {
    suspend fun onChannelOpen(scope: CoroutineScope): ChannelClient.Channel?

    suspend fun onChannelReset(scope: CoroutineScope): ChannelClient.Channel?

    suspend fun onChannelClose(scope: CoroutineScope)

    fun getState(): StateFlow<ChannelClientState>
}
