package com.flipperdevices.keyscreen.emulate.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.info
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import com.flipperdevices.keyscreen.emulate.model.EmulateProgress
import com.flipperdevices.keyscreen.emulate.viewmodel.helpers.AlreadyOpenedAppException
import com.flipperdevices.keyscreen.emulate.viewmodel.helpers.EmulateHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

private const val SUBGHZ_DEFAULT_TIMEOUT_MS = 500L

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
        requestApi: FlipperRequestApi,
        keyType: FlipperKeyType,
        flipperKey: FlipperKey
    ) {
        val appStarted = calculateTimeoutAndStartEmulate(scope, requestApi, flipperKey)

        if (!appStarted) {
            emulateHelper.stopEmulateForce(requestApi)
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
                is EmulateButtonState.Active -> return
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                startEmulateInternal(this, it.requestApi, flipperKey)
            }
        }
    }

    private suspend fun startEmulateInternal(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        flipperKey: FlipperKey
    ) {
        calculateTimeoutAndStartEmulate(scope, requestApi, flipperKey)
        emulateHelper.stopEmulate(viewModelScope, requestApi)
    }

    private suspend fun calculateTimeoutAndStartEmulate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        flipperKey: FlipperKey
    ): Boolean {
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
                requestApi,
                FlipperKeyType.SUB_GHZ,
                flipperKey,
                timeout ?: SUBGHZ_DEFAULT_TIMEOUT_MS
            )
            if (appStarted && timeout != null) {
                emulateButtonStateFlow.emit(
                    EmulateButtonState.Active(
                        progress = EmulateProgress.Growing(timeout)
                    )
                )
            }
        } catch (ignored: AlreadyOpenedAppException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return false
        }

        return appStarted
    }
}
