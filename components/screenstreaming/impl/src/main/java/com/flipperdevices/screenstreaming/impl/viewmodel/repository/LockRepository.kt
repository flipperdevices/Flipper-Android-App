package com.flipperdevices.screenstreaming.impl.viewmodel.repository

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.screenstreaming.api.FScreenStreamingFeatureApi
import com.flipperdevices.bridge.connection.feature.screenstreaming.api.FScreenUnlockFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.screenstreaming.impl.model.ButtonAnimEnum
import com.flipperdevices.screenstreaming.impl.model.FlipperLockState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LockRepository(
    private val scope: CoroutineScope,
    private val stackRepository: ButtonStackRepository,
    private val fFeatureProvider: FFeatureProvider
) : LogTagProvider {
    override val TAG = "LockRepository"

    private val lockStateFlow = MutableStateFlow<FlipperLockState>(FlipperLockState.NotInitialized)

    fun getLockState() = lockStateFlow.asStateFlow()

    fun onChangeLock(isWillBeLocked: Boolean) {
        if (isWillBeLocked) {
            error { "Lock right now not supported" }
            return
        }
        scope.launch {
            val uuid = stackRepository.onNewStackButton(ButtonAnimEnum.UNLOCK)
            val fScreenStreamingFeatureApi = fFeatureProvider.getSync<FScreenUnlockFeatureApi>()
            if (fScreenStreamingFeatureApi == null) {
                error { "#pressOnButton FScreenStreamingFeatureApi not found!" }
                return@launch
            }
            fScreenStreamingFeatureApi.unlock()
                .onFailure { error(it) { "#onChangeLock could not unlock device" } }

            stackRepository.onRemoveStackButton(uuid)
        }
    }

    private fun collectFeatureState() {
        fFeatureProvider.get<FScreenStreamingFeatureApi>()
            .onEach {
                when (it) {
                    FFeatureStatus.Unsupported,
                    FFeatureStatus.NotFound -> {
                        lockStateFlow.emit(FlipperLockState.NotSupported)
                    }

                    FFeatureStatus.Retrieving -> {
                        lockStateFlow.emit(FlipperLockState.NotInitialized)
                    }

                    is FFeatureStatus.Supported -> {
                        lockStateFlow.emit(FlipperLockState.Ready(isLocked = true)) // Always unlock
                    }
                }
            }.launchIn(scope)
    }

    init {
        collectFeatureState()
    }
}
