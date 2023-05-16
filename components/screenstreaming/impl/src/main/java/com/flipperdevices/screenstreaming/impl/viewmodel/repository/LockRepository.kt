package com.flipperdevices.screenstreaming.impl.viewmodel.repository

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.desktop.unlockRequest
import com.flipperdevices.protobuf.main
import com.flipperdevices.screenstreaming.impl.model.ButtonAnimEnum
import com.flipperdevices.screenstreaming.impl.model.FlipperLockState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LockRepository(
    private val scope: CoroutineScope,
    private val stackRepository: ButtonStackRepository,
    private val serviceProvider: FlipperServiceProvider
) : FlipperBleServiceConsumer, LogTagProvider {
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
            val requestApi = serviceProvider.getServiceApi().requestApi
            val response = requestApi.request(
                flowOf(
                    main {
                        desktopUnlockRequest = unlockRequest { }
                    }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
                )
            )

            if (response.commandStatus != Flipper.CommandStatus.OK) {
                error { "Receive failed response: $response" }
            }

            stackRepository.onRemoveStackButton(uuid)
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperVersionApi.getVersionInformationFlow().onEach {
            if (it != null && it >= Constants.API_SUPPORTED_UNLOCK) {
                lockStateFlow.emit(FlipperLockState.Ready(isLocked = true)) // Always unlock
            } else {
                lockStateFlow.emit(FlipperLockState.NotSupported)
            }
        }.launchIn(scope)
    }
}
