package com.flipperdevices.wearable.sync.wear.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.wearable.sync.wear.api.FindPhoneApi
import com.flipperdevices.wearable.sync.wear.api.FindPhoneState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FindPhoneApi::class)
class FindPhoneApiImpl @Inject constructor() : FindPhoneApi {
    private val state = MutableStateFlow<FindPhoneState>(FindPhoneState.Loading)

    override suspend fun update(nodeId: String?) {
        if (nodeId == null) {
            state.emit(FindPhoneState.NotFound)
        } else {
            state.emit(FindPhoneState.Founded(nodeId))
        }
    }

    override fun getState(): StateFlow<FindPhoneState> = state.asStateFlow()
}
