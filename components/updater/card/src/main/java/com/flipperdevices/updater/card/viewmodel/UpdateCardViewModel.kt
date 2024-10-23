package com.flipperdevices.updater.card.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.pb.SelectedChannel
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.card.helpers.StorageExistHelper
import com.flipperdevices.updater.card.helpers.UpdateCardHelper
import com.flipperdevices.updater.card.helpers.UpdateOfferProviderApi
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.UpdateCardState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Suppress("LongParameterList")
class UpdateCardViewModel @AssistedInject constructor(
    private val downloaderApi: DownloaderApi,
    private val flipperVersionProviderApi: FlipperVersionProviderApi,
    private val serviceProvider: FlipperServiceProvider,
    private val dataStoreSettings: DataStore<Settings>,
    private val updateOfferHelper: UpdateOfferProviderApi,
    private val storageExistHelper: StorageExistHelper,
    @Assisted private val deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?
) : DecomposeViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "UpdateCardViewModel"

    private val updateCardState = MutableStateFlow<UpdateCardState>(
        UpdateCardState.InProgress
    )
    private val updateChanelFlow = MutableStateFlow<FirmwareChannel?>(null)

    private val deeplinkFlow = MutableStateFlow(deeplink)

    private var cardStateJob: Job? = null
    private val mutex = Mutex()

    init {
        viewModelScope.launch {
            dataStoreSettings.data.collectLatest {
                updateChanelFlow.emit(it.selected_channel.toFirmwareChannel())
            }
        }
        serviceProvider.provideServiceApi(this, this)
    }

    fun getUpdateCardState(): StateFlow<UpdateCardState> = updateCardState

    fun onSelectChannel(channel: FirmwareChannel?) {
        viewModelScope.launch {
            updateChanelFlow.emit(channel)
            deeplinkFlow.emit(null)
            when (channel) {
                FirmwareChannel.RELEASE,
                FirmwareChannel.RELEASE_CANDIDATE,
                FirmwareChannel.DEV -> dataStoreSettings.updateData {
                    it.copy(
                        selected_channel = channel.toSelectedChannel()
                    )
                }

                else -> {}
            }
        }
    }

    fun refresh() {
        serviceProvider.provideServiceApi(this) {
            launchWithLock(mutex, viewModelScope, "retry") {
                // in this case we get heavy information from fw server and flipper
                // that's why we set state in progress
                updateCardState.emit(UpdateCardState.InProgress)
                cardStateJob?.cancelAndJoin()
                storageExistHelper.invalidate(viewModelScope, it, force = true)
                invalidateUnsafe(it)
            }
        }
    }

    override fun onServiceApiReady(
        serviceApi: FlipperServiceApi
    ) {
        launchWithLock(mutex, viewModelScope, "onServiceApiReady") {
            invalidateUnsafe(serviceApi)
        }
    }

    private suspend fun invalidateUnsafe(serviceApi: FlipperServiceApi) {
        cardStateJob?.cancelAndJoin()
        cardStateJob = null
        cardStateJob = viewModelScope.launch {
            storageExistHelper.invalidate(this, serviceApi, force = false)
            val latestVersionAsync = async {
                val result = runCatching { downloaderApi.getLatestVersion() }
                verbose { "latestVersionAsyncResult: $result" }
                return@async result
            }
            combine(
                flipperVersionProviderApi.getCurrentFlipperVersion(viewModelScope, serviceApi),
                storageExistHelper.isExternalStorageExist(),
                updateOfferHelper.isUpdateRequire(serviceApi),
                updateChanelFlow,
                deeplinkFlow
            ) { flipperFirmwareVersion, isFlashExist, isAlwaysUpdate, updateChannel, deeplink ->
                val newUpdateChannel = if (
                    updateChannel == null && flipperFirmwareVersion != null
                ) {
                    flipperFirmwareVersion.channel
                } else {
                    updateChannel
                }

                return@combine UpdateCardHelper(
                    newUpdateChannel,
                    isFlashExist,
                    flipperFirmwareVersion,
                    isAlwaysUpdate,
                    deeplink,
                    latestVersionAsync
                )
            }.collectLatest {
                val state = it.processUpdateCardState()
                updateCardState.emit(state)
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?
        ): UpdateCardViewModel
    }
}

private fun SelectedChannel.toFirmwareChannel(): FirmwareChannel? = when (this) {
    SelectedChannel.RELEASE -> FirmwareChannel.RELEASE
    SelectedChannel.RELEASE_CANDIDATE -> FirmwareChannel.RELEASE_CANDIDATE
    SelectedChannel.DEV -> FirmwareChannel.DEV
    is SelectedChannel.Unrecognized -> null
}

private fun FirmwareChannel?.toSelectedChannel(): SelectedChannel = when (this) {
    FirmwareChannel.RELEASE -> SelectedChannel.RELEASE
    FirmwareChannel.RELEASE_CANDIDATE -> SelectedChannel.RELEASE_CANDIDATE
    FirmwareChannel.DEV -> SelectedChannel.DEV
    FirmwareChannel.UNKNOWN,
    FirmwareChannel.CUSTOM -> error("Can`t convert unknown firmware channel to internal channel")

    null -> SelectedChannel.fromValue(-1)
}
