package com.flipperdevices.updater.card.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.jre.extension
import com.flipperdevices.core.ktx.jre.filename
import com.flipperdevices.core.ktx.jre.length
import com.flipperdevices.core.ktx.jre.nameWithoutExtension
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.card.model.BatteryState
import com.flipperdevices.updater.card.model.SyncingState
import com.flipperdevices.updater.card.model.UpdatePending
import com.flipperdevices.updater.card.model.UpdatePendingState
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.InternalStorageFirmware
import com.flipperdevices.updater.model.UpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

private const val EXT_UPDATER_FILE = "tgz"
private const val SIZE_FOLDER_UPDATE_MAX = 1024L * 1024L * 1024L * 10 // 10Mb

class UpdateRequestViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    private val updaterUIApi: UpdaterUIApi,
    private val synchronizationApi: SynchronizationApi
) : LifecycleViewModel(), FlipperBleServiceConsumer {

    private val batteryFlow = MutableStateFlow<BatteryState>(BatteryState.Unknown)
    fun getBatteryState(): StateFlow<BatteryState> = batteryFlow
    private val pendingFlow = MutableStateFlow<UpdatePendingState?>(null)
    fun getUpdatePendingState(): StateFlow<UpdatePendingState?> = pendingFlow

    init {
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperInformationApi.getInformationFlow().onEach {
            val batteryLevel = it.batteryLevel
            val state = if (batteryLevel == null) BatteryState.Unknown
            else BatteryState.Ready(it.isCharging, batteryLevel)
            batteryFlow.emit(state)
        }.launchIn(viewModelScope)
    }

    fun onUpdateRequest(updatePending: UpdatePending) {
        viewModelScope.launch {
            if (batteryFlow.value.isAllowToUpdate().not()) {
                pendingFlow.emit(UpdatePendingState.LowBattery)
                return@launch
            }
            when (updatePending) {
                is UpdatePending.Request -> startUpdateFromServer(updatePending)
                is UpdatePending.URI -> startUpdateFromFile(updatePending)
            }
        }
    }

    fun openUpdate(update: UpdateRequest) {
        viewModelScope.launch {
            updaterUIApi.openUpdateScreen(updateRequest = update)
        }
    }

    fun stopSyncAndStartUpdate(request: UpdateRequest) {
        viewModelScope.launch {
            pendingFlow.emit(UpdatePendingState.Ready(request, SyncingState.Stop))
        }
    }

    fun resetState() {
        viewModelScope.launch { pendingFlow.emit(null) }
    }

    private suspend fun startUpdateFromServer(updatePending: UpdatePending.Request) {
        val isSyncing = synchronizationApi.isSynchronizationRunning()
        val syncState = if (isSyncing) SyncingState.InProgress else SyncingState.Complete
        pendingFlow.emit(UpdatePendingState.Ready(updatePending.updateRequest, syncState))
    }

    private fun startUpdateFromFile(updatePending: UpdatePending.URI) {
        viewModelScope.launch {
            val context = updatePending.context
            val sizeFile = updatePending.uri.length(context.contentResolver)
            val filename = updatePending.uri.filename(context.contentResolver)
            val extension = filename.extension()

            if (extension != EXT_UPDATER_FILE) {
                pendingFlow.emit(UpdatePendingState.FileExtension)
                return@launch
            }

            if (sizeFile == null || sizeFile > SIZE_FOLDER_UPDATE_MAX) {
                pendingFlow.emit(UpdatePendingState.FileBig)
                return@launch
            }

            val request = UpdateRequest(
                updateFrom = updatePending.currentVersion,
                updateTo = FirmwareVersion(
                    channel = FirmwareChannel.CUSTOM,
                    version = filename.nameWithoutExtension()
                ),
                changelog = null,
                content = InternalStorageFirmware(updatePending.uri)
            )

            val isSyncing = synchronizationApi.isSynchronizationRunning()
            val syncState = if (isSyncing) SyncingState.InProgress else SyncingState.Complete
            pendingFlow.emit(UpdatePendingState.Ready(request, syncState))
        }
    }
}
