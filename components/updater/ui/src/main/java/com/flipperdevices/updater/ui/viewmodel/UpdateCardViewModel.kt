package com.flipperdevices.updater.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.api.UpdateCardApi
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.ui.di.UpdaterComponent
import javax.inject.Inject
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

    override fun onServiceApiReady(
        serviceApi: FlipperServiceApi
    ) = launchWithLock(mutex, viewModelScope) {
        cardStateJob?.cancelAndJoin()
        cardStateJob = null
        cardStateJob = viewModelScope.launch(Dispatchers.Default) {
            val latestVersionAsync = async { downloaderApi.getLatestVersion() }
            serviceApi.flipperRpcInformationApi.getRpcInformationFlow()
            combine(
                updateChannel,
                flipperVersionProviderApi
                    .getCurrentFlipperVersion(viewModelScope, serviceApi)
            ) { updateChannel, flipperFirmwareVersion ->
                val newUpdateChannel = if (
                    updateChannel == null && flipperFirmwareVersion != null
                ) {
                    flipperFirmwareVersion.channel
                } else updateChannel
                return@combine newUpdateChannel to flipperFirmwareVersion
            }.collectLatest { (updateChannel, flipperFirmwareVersion) ->
                info { "Receive version from flipper $flipperFirmwareVersion" }
                if (flipperFirmwareVersion == null) {
                    updateCardState.emit(UpdateCardState.InProgress)
                    return@collectLatest
                }
                val latestVersionFromNetwork = latestVersionAsync.await()[updateChannel]
                info { "Latest version from network is $latestVersionFromNetwork" }
                if (latestVersionFromNetwork == null) {
                    updateCardState.emit(UpdateCardState.NoUpdate(flipperFirmwareVersion))
                    return@collectLatest
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
        }
    }
}
