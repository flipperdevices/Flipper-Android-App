package com.flipperdevices.keyscreen.emulate.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyscreen.api.EmulateProgress
import com.flipperdevices.keyscreen.api.emulate.AlreadyOpenedAppException
import com.flipperdevices.keyscreen.api.emulate.EmulateHelper
import com.flipperdevices.keyscreen.api.emulate.ForbiddenFrequencyException
import com.flipperdevices.keyscreen.api.emulate.SUBGHZ_DEFAULT_TIMEOUT_MS
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class SubGhzViewModel @VMInject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    private val keyParser: KeyParser,
    synchronizationApi: SynchronizationApi,
    application: Application
) : EmulateViewModel(serviceProvider, emulateHelper, synchronizationApi, application) {
    override val TAG = "SubGhzViewModel"

    override suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        keyType: FlipperKeyType,
        flipperKey: FlipperKey
    ) {
        val appStarted = calculateTimeoutAndStartEmulate(
            scope = scope,
            serviceApi = serviceApi,
            flipperKey = flipperKey,
            oneTimePress = false
        )

        if (!appStarted) {
            emulateHelper.stopEmulateForce(serviceApi.requestApi)
        }
    }

    fun onSinglePress(flipperKey: FlipperKey) {
        info { "#onSinglePress" }
        vibrator?.vibrateCompat(VIBRATOR_TIME)
        val fileType = flipperKey.path.keyType ?: return
        if (fileType != FlipperKeyType.SUB_GHZ) {
            return
        }

        emulateButtonStateFlow.update {
            when (it) {
                is EmulateButtonState.Disabled,
                is EmulateButtonState.Loading -> it
                is EmulateButtonState.Inactive -> EmulateButtonState.Active(
                    EmulateProgress.Infinite
                )
                is EmulateButtonState.Active -> {
                    super.onStopEmulate(force = true)
                    return
                }
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                startEmulateInternal(this, it, flipperKey)
            }
        }
    }

    private suspend fun startEmulateInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        flipperKey: FlipperKey
    ) {
        calculateTimeoutAndStartEmulate(scope, serviceApi, flipperKey, oneTimePress = true)
        emulateHelper.stopEmulate(viewModelScope, serviceApi.requestApi)
    }

    private suspend fun calculateTimeoutAndStartEmulate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        flipperKey: FlipperKey,
        oneTimePress: Boolean
    ): Boolean {
        val requestApi = serviceApi.requestApi
        val parsedKey = keyParser.parseKey(flipperKey)

        val timeout = if (parsedKey is FlipperKeyParsed.SubGhz &&
            parsedKey.totalTimeMs != null
        ) {
            parsedKey.totalTimeMs
        } else null

        var appStarted: Boolean? = null

        try {
            appStarted = emulateHelper.startEmulate(
                scope,
                serviceApi,
                FlipperKeyType.SUB_GHZ,
                flipperKey.path,
                timeout ?: SUBGHZ_DEFAULT_TIMEOUT_MS
            )
            if (appStarted && timeout != null) {
                if (oneTimePress) {
                    emulateButtonStateFlow.emit(
                        EmulateButtonState.Active(
                            progress = EmulateProgress.GrowingAndStop(timeout)
                        )
                    )
                } else emulateButtonStateFlow.emit(
                    EmulateButtonState.Active(
                        progress = EmulateProgress.Growing(timeout)
                    )
                )
            }
        } catch (ignored: AlreadyOpenedAppException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return false
        } catch (ignored: ForbiddenFrequencyException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.ForbiddenFrequencyDialog)
            return false
        } catch (fatal: Throwable) {
            error(fatal) { "Handle fatal exception on emulate subghz" }
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
            return false
        }

        return appStarted
    }
}
