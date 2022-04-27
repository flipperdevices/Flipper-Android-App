package com.flipperdevices.updater.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.api.UpdateCardApi
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.VersionFiles
import com.flipperdevices.updater.ui.R
import com.flipperdevices.updater.ui.di.UpdaterComponent
import java.net.UnknownHostException
import java.util.EnumMap
import javax.inject.Inject
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

class UpdateCardViewModel :
    LifecycleViewModel(),
    FlipperBleServiceConsumer,
    UpdateCardApi,
    LogTagProvider {
    override val TAG = "UpdaterViewModel"

    private val updateCardState = MutableStateFlow<UpdateCardState>(
        UpdateCardState.InProgress
    )
    private val updateChannel = MutableStateFlow<FirmwareChannel?>(null)
    private var cardStateJob: Job? = null
    private val mutex = Mutex()

    @Inject
    lateinit var downloaderApi: DownloaderApi

    @Inject
    lateinit var flipperVersionProviderApi: FlipperVersionProviderApi

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<UpdaterComponent>().inject(this)
        serviceProvider.provideServiceApi(this, this)
    }

    override fun getUpdateCardState(): StateFlow<UpdateCardState> = updateCardState

    override fun onSelectChannel(channel: FirmwareChannel?) {
        viewModelScope.launch {
            updateChannel.emit(channel)
        }
    }

    override fun retry() {
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
                updateChannel,
                flipperVersionProviderApi.getCurrentFlipperVersion(viewModelScope, serviceApi),
                serviceApi.flipperRpcInformationApi.getRpcInformationFlow()
            ) { updateChannel, flipperFirmwareVersion, rpcInformation ->
                val newUpdateChannel = if (
                    updateChannel == null && flipperFirmwareVersion != null
                ) {
                    flipperFirmwareVersion.channel
                } else updateChannel
                return@combine Triple(
                    newUpdateChannel,
                    flipperFirmwareVersion,
                    if (rpcInformation.externalStorageStats != null) {
                        rpcInformation.externalStorageStats is StorageStats.Loaded
                    } else null
                )
            }.collectLatest { (updateChannel, flipperFirmwareVersion, isFlashExist) ->
                updateCardState(
                    updateChannel,
                    flipperFirmwareVersion,
                    latestVersionAsync,
                    isFlashExist
                )
            }
        }
    }

    private suspend fun updateCardState(
        updateChannel: FirmwareChannel?,
        flipperFirmwareVersion: FirmwareVersion?,
        latestVersionAsync: Deferred<Result<EnumMap<FirmwareChannel, VersionFiles>>>,
        isFlashExist: Boolean?
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
                UpdateCardState.Error(
                    iconId = R.drawable.ic_no_sd,
                    titleId = R.string.update_card_error_no_sd_title,
                    descriptionId = R.string.update_card_error_no_sd_desc
                )
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
        val isUpdateAvailable = true

        if (isUpdateAvailable) {
            updateCardState.emit(
                UpdateCardState.UpdateAvailable(
                    lastVersion = latestVersionFromNetwork.version,
                    updaterDist = latestVersionFromNetwork.updaterFile,
                    isOtherChannel = latestVersionFromNetwork.version.channel
                        != flipperFirmwareVersion.channel
                )
            )
        } else updateCardState.emit(UpdateCardState.NoUpdate(flipperFirmwareVersion))
    }

    private suspend fun processNetworkException(exception: Throwable) {
        if (exception is UnknownHostException) {
            updateCardState.emit(
                UpdateCardState.Error(
                    iconId = R.drawable.ic_no_internet,
                    titleId = R.string.update_card_error_no_internet_title,
                    descriptionId = R.string.update_card_error_no_internet_desc
                )
            )
            return
        } else {
            error(exception) { "Error while getting latest version from network" }
            updateCardState.emit(
                UpdateCardState.Error(
                    iconId = R.drawable.ic_server_error,
                    titleId = R.string.update_card_error_server_request_title,
                    descriptionId = R.string.update_card_error_server_request_desc
                )
            )
            return
        }
    }
}
