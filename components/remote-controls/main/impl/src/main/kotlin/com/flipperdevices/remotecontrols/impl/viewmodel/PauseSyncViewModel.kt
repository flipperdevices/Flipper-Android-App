package com.flipperdevices.remotecontrols.impl.viewmodel

import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class PauseSyncViewModel @Inject constructor(
    private val synchronizationApi: SynchronizationApi
) : DecomposeViewModel() {

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()
    val isDialogVisible = state.map {
        when (it) {
            State.Pending -> false
            State.Visible -> true
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun stop() {
        viewModelScope.launch {
            _state.emit(State.Pending)
            synchronizationApi.stop()
        }
    }

    fun dismiss() {
        _state.update { State.Pending }
    }

    private fun onSynchronizing(state: SynchronizationState) {
        when (state) {
            SynchronizationState.NotStarted,
            SynchronizationState.Finished -> {
                _state.update { State.Pending }
            }

            is SynchronizationState.InProgress -> {
                _state.update { State.Visible }
            }
        }
    }

    init {
        synchronizationApi.getSynchronizationState()
            .distinctUntilChangedBy {
                when (it) {
                    SynchronizationState.Finished -> 0
                    is SynchronizationState.InProgress -> 1
                    SynchronizationState.NotStarted -> 2
                }
            }
            .onEach { onSynchronizing(it) }
            .launchIn(viewModelScope)
    }

    sealed interface State {
        data object Pending : State
        data object Visible : State
    }
}
