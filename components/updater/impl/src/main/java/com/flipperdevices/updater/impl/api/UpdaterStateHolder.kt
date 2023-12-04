package com.flipperdevices.updater.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.model.UpdatingStateWithRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

interface UpdaterStateHolder {
    fun getState(): StateFlow<UpdatingStateWithRequest>

    suspend fun updateState(state: UpdatingStateWithRequest)

    fun update(function: (UpdatingStateWithRequest) -> UpdatingStateWithRequest)
}

@Singleton
@ContributesBinding(AppGraph::class, UpdaterStateHolder::class)
class UpdaterStateHolderImpl @Inject constructor() : UpdaterStateHolder {
    private val updatingState = MutableStateFlow(
        UpdatingStateWithRequest(UpdatingState.NotStarted, request = null)
    )

    override fun getState(): StateFlow<UpdatingStateWithRequest> {
        return updatingState.asStateFlow()
    }

    override suspend fun updateState(state: UpdatingStateWithRequest) {
        updatingState.emit(state)
    }

    override fun update(function: (UpdatingStateWithRequest) -> UpdatingStateWithRequest) {
        updatingState.update(function)
    }
}
