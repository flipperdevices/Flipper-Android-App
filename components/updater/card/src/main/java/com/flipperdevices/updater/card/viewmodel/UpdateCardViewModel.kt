package com.flipperdevices.updater.card.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.then
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.pb.SelectedChannel
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.card.helpers.UpdateOfferProviderApi
import com.flipperdevices.updater.card.utils.isGreaterThan
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateErrorType
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.VersionFiles
import java.net.UnknownHostException
import java.util.EnumMap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import tangle.viewmodel.VMInject

class UpdateCardViewModel @VMInject constructor(
    private val downloaderApi: DownloaderApi,
    private val flipperVersionProviderApi: FlipperVersionProviderApi,
    private val serviceProvider: FlipperServiceProvider,
    private val dataStoreSettings: DataStore<Settings>,
    private val updateOfferHelper: UpdateOfferProviderApi
) :
    LifecycleViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "UpdateCardViewModel"

    private val updateCardState = MutableStateFlow<UpdateCardState>(
        UpdateCardState.InProgress
    )
    private val updateChanelFlow = MutableStateFlow<FirmwareChannel?>(null)

    private var cardStateJob: Job? = null
    private val mutex = Mutex()

    init {
        viewModelScope.launch {
            dataStoreSettings.data.collectLatest {
                updateChanelFlow.emit(it.selectedChannel.toFirmwareChannel())
            }
        }
        serviceProvider.provideServiceApi(this, this)
    }

    fun getUpdateCardState(): StateFlow<UpdateCardState> = updateCardState

    fun onSelectChannel(channel: FirmwareChannel?) {
        viewModelScope.launch {
            updateChanelFlow.emit(channel)
            when (channel) {
                FirmwareChannel.RELEASE,
                FirmwareChannel.RELEASE_CANDIDATE,
                FirmwareChannel.DEV -> dataStoreSettings.updateData {
                    it.toBuilder()
                        .setSelectedChannel(channel.toSelectedChannel())
                        .build()
                }
                else -> {}
            }
        }
    }

    fun retry() {
        serviceProvider.provideServiceApi(this) {
            launchWithLock(mutex, viewModelScope, "retry") {
                // in this case we get heavy information from fw server and flipper
                // that's why we set state in progress
                updateCardState.emit(UpdateCardState.InProgress)
                cardStateJob?.cancelAndJoin()
                it.flipperRpcInformationApi.invalidate(it.requestApi)
                invalidateUnsafe(it)
            }
        }
    }

    override fun onServiceApiReady(
        serviceApi: FlipperServiceApi
    ) = launchWithLock(mutex, viewModelScope, "onServiceApiReady") {
        invalidateUnsafe(serviceApi)
    }

    private suspend fun invalidateUnsafe(serviceApi: FlipperServiceApi) {
        cardStateJob?.cancelAndJoin()
        cardStateJob = null
        cardStateJob = viewModelScope.launch(Dispatchers.Default) {
            val latestVersionAsync = async {
                val result = runCatching { downloaderApi.getLatestVersion() }
                verbose { "latestVersionAsyncResult: $result" }
                return@async result
            }
            combine(
                flipperVersionProviderApi.getCurrentFlipperVersion(viewModelScope, serviceApi),
                serviceApi.flipperRpcInformationApi.getRpcInformationFlow(),
                updateOfferHelper.isUpdateRequire(serviceApi),
                updateChanelFlow
            ) { flipperFirmwareVersion, rpcInformation, isAlwaysUpdate, updateChannel ->
                val newUpdateChannel = if (
                    updateChannel == null && flipperFirmwareVersion != null
                ) {
                    flipperFirmwareVersion.channel
                } else updateChannel
                val isFlashExist = if (rpcInformation.externalStorageStats != null) {
                    rpcInformation.externalStorageStats is StorageStats.Loaded
                } else null
                return@combine newUpdateChannel
                    .then(flipperFirmwareVersion)
                    .then(isFlashExist)
                    .then(isAlwaysUpdate)
            }.collectLatest { (updateChannel, flipperFirmwareVersion, isFlashExist, alwaysUpdate) ->
                updateCardState(
                    updateChannel,
                    flipperFirmwareVersion,
                    latestVersionAsync,
                    isFlashExist,
                    alwaysUpdate
                )
            }
        }
    }

    private suspend fun updateCardState(
        updateChannel: FirmwareChannel?,
        flipperFirmwareVersion: FirmwareVersion?,
        latestVersionAsync: Deferred<Result<EnumMap<FirmwareChannel, VersionFiles>>>,
        isFlashExist: Boolean?,
        alwaysShowUpdate: Boolean
    ) {
        info { "Receive version from flipper $flipperFirmwareVersion" }
        if (flipperFirmwareVersion == null) {
            updateCardState.emit(UpdateCardState.InProgress)
            return
        }
        if (isFlashExist == null) {
            updateCardState.emit(UpdateCardState.InProgress)
            return
        } else if (!isFlashExist) {
            updateCardState.emit(
                UpdateCardState.Error(UpdateErrorType.NO_SD_CARD)
            )
            return
        }

        val latestVersionFromNetworkResult = latestVersionAsync.await()
        val exception = latestVersionFromNetworkResult.exceptionOrNull()
        if (exception != null) {
            processNetworkException(exception)
            return
        }

        if (updateChannel == FirmwareChannel.CUSTOM) {
            updateCardState.emit(
                UpdateCardState.CustomUpdate(
                    flipperVersion = flipperFirmwareVersion,
                    updateVersion = FirmwareVersion(channel = FirmwareChannel.CUSTOM, version = "")
                )
            )
            return
        }

        val latestVersionFromNetwork = latestVersionFromNetworkResult
            .getOrNull()?.get(updateChannel)
        info { "Latest version from network is ${latestVersionFromNetwork?.version}" }
        if (latestVersionFromNetwork == null) {
            updateCardState.emit(UpdateCardState.NoUpdate(flipperFirmwareVersion))
            return
        }
        val isUpdateAvailable = alwaysShowUpdate ||
            latestVersionFromNetwork.version.isGreaterThan(flipperFirmwareVersion) ?: true ||
            updateChannel == FirmwareChannel.UNKNOWN

        if (isUpdateAvailable) {
            updateCardState.emit(
                UpdateCardState.UpdateAvailable(
                    update = UpdateRequest(
                        updateFrom = flipperFirmwareVersion,
                        updateTo = latestVersionFromNetwork.version,
                        changelog = latestVersionFromNetwork.changelog,
                        content = OfficialFirmware(latestVersionFromNetwork.updaterFile)
                    ),
                    isOtherChannel = latestVersionFromNetwork.version.channel
                        != flipperFirmwareVersion.channel
                )
            )
        } else updateCardState.emit(UpdateCardState.NoUpdate(flipperFirmwareVersion))
    }

    private suspend fun processNetworkException(exception: Throwable) {
        if (exception is UnknownHostException) {
            updateCardState.emit(
                UpdateCardState.Error(UpdateErrorType.NO_INTERNET)
            )
            return
        } else {
            error(exception) { "Error while getting latest version from network" }
            updateCardState.emit(
                UpdateCardState.Error(UpdateErrorType.UNABLE_TO_SERVER)
            )
            return
        }
    }
}

private fun SelectedChannel.toFirmwareChannel(): FirmwareChannel? = when (this) {
    SelectedChannel.RELEASE -> FirmwareChannel.RELEASE
    SelectedChannel.RELEASE_CANDIDATE -> FirmwareChannel.RELEASE_CANDIDATE
    SelectedChannel.DEV -> FirmwareChannel.DEV
    SelectedChannel.UNRECOGNIZED -> null
}

private fun FirmwareChannel?.toSelectedChannel(): SelectedChannel = when (this) {
    FirmwareChannel.RELEASE -> SelectedChannel.RELEASE
    FirmwareChannel.RELEASE_CANDIDATE -> SelectedChannel.RELEASE_CANDIDATE
    FirmwareChannel.DEV -> SelectedChannel.DEV
    FirmwareChannel.UNKNOWN,
    FirmwareChannel.CUSTOM -> error("Can`t convert unknown firmware channel to internal channel")
    null -> SelectedChannel.UNRECOGNIZED
}
