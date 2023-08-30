package com.flipperdevices.wearable.sync.wear.api

import kotlinx.coroutines.flow.StateFlow

interface FindPhoneApi {
    suspend fun update(nodeId: String?)
    fun getState(): StateFlow<FindPhoneState>
}

sealed class FindPhoneState {
    data object Loading : FindPhoneState()
    data object NotFound : FindPhoneState()
    data class Founded(val nodeId: String) : FindPhoneState()
}
