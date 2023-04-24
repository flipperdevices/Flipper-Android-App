package com.flipperdevices.keyscreen.emulate.viewmodel.type

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
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
import com.flipperdevices.keyscreen.api.emulate.INFRARED_DEFAULT_TIMEOUT_MS
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import com.flipperdevices.keyscreen.emulate.viewmodel.EmulateViewModel
import com.flipperdevices.keyscreen.emulate.viewmodel.VIBRATOR_TIME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class InfraredViewModel @VMInject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    synchronizationApi: SynchronizationApi,
    application: Application
) : EmulateViewModel(serviceProvider, emulateHelper, synchronizationApi, application) {
    override val TAG = "InfraredViewModel"

    override suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        keyType: FlipperKeyType,
        flipperKey: FlipperKey
    ) = Unit

    fun onSinglePress(flipperKey: FlipperKey, name: String) {
        info { "#onSinglePress" }
        vibrator?.vibrateCompat(VIBRATOR_TIME)
        val fileType = flipperKey.path.keyType ?: return
        if (fileType != FlipperKeyType.INFRARED) {
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
                startEmulateInternal(this, it, flipperKey, name)
            }
        }
    }

    private suspend fun startEmulateInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        flipperKey: FlipperKey,
        name: String
    ) {
        startEmulate(scope, serviceApi, flipperKey, name)
        emulateHelper.stopEmulate(viewModelScope, serviceApi.requestApi)
    }

    private suspend fun startEmulate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        flipperKey: FlipperKey,
        name: String,
    ): Boolean {
        val requestApi = serviceApi.requestApi
        val appStarted: Boolean?

        try {
            appStarted = emulateHelper.startEmulate(
                scope,
                serviceApi,
                FlipperKeyType.INFRARED,
                flipperKey.path,
                INFRARED_DEFAULT_TIMEOUT_MS,
                args = name
            )
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
