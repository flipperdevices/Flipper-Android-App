package com.flipperdevices.keyemulate.viewmodel

import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.AlreadyOpenedAppException
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.ForbiddenFrequencyException
import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.model.FlipperSupportedState
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyemulate.model.DisableButtonReason
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.keyemulate.model.LoadingState
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
import com.flipperdevices.protobuf.app.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.app.Application as FlipperApp

const val VIBRATOR_TIME = 100L

abstract class EmulateViewModel(
    private val synchronizationApi: SynchronizationApi,
    private val closeEmulateAppTaskHolder: CloseEmulateAppTaskHolder,
    application: FlipperApp,
    private val settings: DataStore<Settings>,
    protected val fFeatureProvider: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : DecomposeViewModel(), LogTagProvider {

    protected val emulateButtonStateFlow =
        MutableStateFlow<EmulateButtonState>(EmulateButtonState.Loading(LoadingState.CONNECTING))

    protected val vibrator = ContextCompat.getSystemService(application, Vibrator::class.java)

    fun getEmulateButtonStateFlow(): StateFlow<EmulateButtonState> = emulateButtonStateFlow

    open fun onStartEmulate(
        config: EmulateConfig
    ) {
        info { "#onStartEmulate" }
        vibrator?.vibrateCompat(
            VIBRATOR_TIME,
            runBlocking { settings.data.first().disabled_vibration }
        )

        emulateButtonStateFlow.update {
            when (it) {
                is EmulateButtonState.Disabled -> it
                is EmulateButtonState.Loading -> it
                is EmulateButtonState.Inactive -> EmulateButtonState.Active(
                    progress = EmulateProgress.Infinite,
                    config = config
                )

                is EmulateButtonState.Active -> return
            }
        }

        viewModelScope.launch {
            onStartEmulateInternal(this, config)
        }
    }

    protected open suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        config: EmulateConfig
    ) {
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()
        info { "#onStartEmulateInternal" }
        val emulateStarted = try {
            emulateHelper.startEmulate(
                scope,
                config
            )
        } catch (ignored: AlreadyOpenedAppException) {
            emulateHelper.stopEmulateForce()
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return
        } catch (ignored: ForbiddenFrequencyException) {
            emulateHelper.stopEmulateForce()
            emulateButtonStateFlow.emit(EmulateButtonState.ForbiddenFrequencyDialog)
            return
        } catch (fatal: Throwable) {
            error(fatal) { "Unhandled exception on emulate" }
            emulateHelper.stopEmulateForce()
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
            return
        }
        if (!emulateStarted) {
            info { "Failed start emulation" }
            emulateHelper.stopEmulateForce()
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
        }
    }

    fun onStopEmulate(force: Boolean = false) {
        info { "#onStopEmulate" }
        viewModelScope.launch {
            val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
                error { "#onStartEmulateInternal could not get emulate api" }
                return@launch
            }
            val emulateHelper = fEmulateApi.getEmulateHelper()
            if (force) {
                emulateHelper.stopEmulateForce()
            } else {
                emulateHelper.stopEmulate(viewModelScope)
            }
        }
        vibrator?.vibrateCompat(
            VIBRATOR_TIME,
            runBlocking { settings.data.first().disabled_vibration }
        )
    }

    fun closeDialog() {
        emulateButtonStateFlow.update {
            if (it == EmulateButtonState.AppAlreadyOpenDialog ||
                it == EmulateButtonState.ForbiddenFrequencyDialog
            ) {
                EmulateButtonState.Inactive()
            } else {
                it
            }
        }
    }

    init {
        combine(
            fFeatureProvider.get<FVersionFeatureApi>()
                .map { status -> status as? FFeatureStatus.Supported<FVersionFeatureApi> }
                .map { status -> status?.featureApi },
            fDeviceOrchestrator.getState(),
            synchronizationApi.getSynchronizationState(),
            fFeatureProvider.get<FEmulateFeatureApi>()
                .map { status -> status as? FFeatureStatus.Supported<FEmulateFeatureApi> }
                .map { status -> status?.featureApi }
                .flatMapLatest { feature ->
                    feature?.isInfraredEmulationSupported ?: flowOf(false)
                },
        ) { versionInformation, connectionState, synchronizationState, isInfraredEmulationSupported ->

            return@combine if (connectionState is FDeviceConnectStatus.Disconnected) {
                EmulateButtonState.Disabled(DisableButtonReason.NOT_CONNECTED)
            } else if (connectionState !is FDeviceConnectStatus.Connected ||
                versionInformation?.getSupportedStateFlow()?.first() != FlipperSupportedState.READY
            ) {
                EmulateButtonState.Loading(LoadingState.CONNECTING)
            } else if (synchronizationState is SynchronizationState.InProgress) {
                EmulateButtonState.Loading(LoadingState.SYNCING)
            } else if (!isInfraredEmulationSupported) {
                EmulateButtonState.Disabled(DisableButtonReason.UPDATE_FLIPPER)
            } else {
                EmulateButtonState.Inactive()
            }
        }.onEach { emulateButtonState ->
            emulateButtonStateFlow.emit(emulateButtonState)
        }.launchIn(viewModelScope)

        fFeatureProvider.get<FEmulateFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported }
            .map { status -> status?.featureApi }
            .flatMapLatest { feature ->
                feature?.getAppEmulateHelper()?.appStateFlow() ?: flowOf(
                    null
                )
            }
            .onEach { appStateResponse ->
                if (appStateResponse?.state == AppState.APP_CLOSED) {
                    emulateButtonStateFlow.update {
                        if (it is EmulateButtonState.Active) {
                            EmulateButtonState.Inactive()
                        } else {
                            it
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    override fun onDestroy() {
        if (emulateButtonStateFlow.value is EmulateButtonState.Active) {
            closeEmulateAppTaskHolder.closeEmulateApp()
        }
        super.onDestroy()
    }
}
