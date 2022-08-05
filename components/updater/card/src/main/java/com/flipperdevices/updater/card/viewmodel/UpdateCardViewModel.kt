package com.flipperdevices.updater.card.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.then
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.SelectedChannel
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.md5sumRequest
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.card.di.CardComponent
import com.flipperdevices.updater.card.utils.isGreaterThan
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateErrorType
import com.flipperdevices.updater.model.VersionFiles
import java.net.UnknownHostException
import java.util.EnumMap
import javax.inject.Inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

private const val PATH_TO_MANIFEST = "/ext/Manifest"

class UpdateCardViewModel :
    LifecycleViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "UpdaterViewModel"

    private val updateCardState = MutableStateFlow<UpdateCardState>(
        UpdateCardState.InProgress
    )
    private var cardStateJob: Job? = null
    private val mutex = Mutex()

    @Inject
    lateinit var downloaderApi: DownloaderApi

    @Inject
    lateinit var flipperVersionProviderApi: FlipperVersionProviderApi

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var dataStoreSettings: DataStore<Settings>

    init {
        ComponentHolder.component<CardComponent>().inject(this)
        serviceProvider.provideServiceApi(this, this)
    }

    fun getUpdateCardState(): StateFlow<UpdateCardState> = updateCardState

    fun onSelectChannel(channel: FirmwareChannel?) {
        viewModelScope.launch {
            dataStoreSettings.updateData {
                it.toBuilder()
                    .setSelectedChannel(channel.toSelectedChannel())
                    .build()
            }
        }
    }

    fun retry() {
        serviceProvider.provideServiceApi(this) {
            launchWithLock(mutex, viewModelScope, "retry") {
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
                info { "latestVersionAsyncResult: $result" }
                return@async result
            }
            serviceApi.flipperRpcInformationApi.getRpcInformationFlow()
            combine(
                dataStoreSettings.data.map { it.selectedChannel.toFirmwareChannel() },
                flipperVersionProviderApi.getCurrentFlipperVersion(viewModelScope, serviceApi),
                serviceApi.flipperRpcInformationApi.getRpcInformationFlow(),
                dataStoreSettings.data,
                isManifestExist(serviceApi)
            ) { updateChannel, flipperFirmwareVersion, rpcInformation, settings, isManifestExist ->
                val newUpdateChannel = if (
                    updateChannel == null && flipperFirmwareVersion != null
                ) {
                    flipperFirmwareVersion.channel
                } else updateChannel
                val isFlashExist = if (rpcInformation.externalStorageStats != null) {
                    rpcInformation.externalStorageStats is StorageStats.Loaded
                } else null
                return@combine newUpdateChannel.then(flipperFirmwareVersion)
                    .then(isFlashExist)
                    .then(settings.alwaysUpdate || !isManifestExist)
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

        val latestVersionFromNetwork = latestVersionFromNetworkResult
            .getOrNull()?.get(updateChannel)
        info { "Latest version from network is $latestVersionFromNetwork" }
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
                    fromVersion = flipperFirmwareVersion,
                    lastVersion = latestVersionFromNetwork,
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

    private fun isManifestExist(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return serviceApi.requestApi.request(
            main {
                storageMd5SumRequest = md5sumRequest {
                    path = PATH_TO_MANIFEST
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).map { response ->
            // if md5sum return not ok, we suppose assets not exist
            val exist = (response.commandStatus == Flipper.CommandStatus.OK)
            info { "Exist manifest response: $exist" }
            exist
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
    FirmwareChannel.UNKNOWN -> error("Can`t convert unknown firmware channel to internal channel")
    null -> SelectedChannel.UNRECOGNIZED
}
