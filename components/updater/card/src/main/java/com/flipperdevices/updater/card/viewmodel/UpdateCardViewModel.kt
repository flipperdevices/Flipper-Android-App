package com.flipperdevices.updater.card.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Suppress("LongParameterList")
class UpdateCardViewModel @AssistedInject constructor(
    private val downloaderApi: DownloaderApi,
    private val flipperVersionProviderApi: FlipperVersionProviderApi,
    private val dataStoreSettings: DataStore<Settings>,
    private val updateOfferHelper: UpdateOfferProviderApi,
    private val storageExistHelper: StorageExistHelper,
    @Assisted private val deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?,
    private val fFeatureProvider: FFeatureProvider
) : DecomposeViewModel(),
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
        launchWithLock(mutex, viewModelScope, "retry") {
            // in this case we get heavy information from fw server and flipper
            // that's why we set state in progress
            updateCardState.emit(UpdateCardState.InProgress)
            cardStateJob?.cancelAndJoin()
            storageExistHelper.invalidate(viewModelScope, force = true)
            invalidateUnsafe(
                fStorageFeatureApi = fFeatureProvider.getSync<FStorageFeatureApi>() ?: run {
                    error { "#refresh could not get FStorageFeatureApi" }
                    return@launchWithLock
                }
            )
        }
    }

    init {
        fFeatureProvider.get<FStorageFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FStorageFeatureApi> }
            .onEach { fStorageFeatureStatus ->
                if (fStorageFeatureStatus == null) {
                    cardStateJob?.cancelAndJoin()
                    cardStateJob = null
                } else {
                    launchWithLock(mutex, viewModelScope, "onServiceApiReady") {
                        invalidateUnsafe(
                            fStorageFeatureApi = fStorageFeatureStatus.featureApi
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    private suspend fun invalidateUnsafe(
        fStorageFeatureApi: FStorageFeatureApi
    ) {
        cardStateJob?.cancelAndJoin()
        cardStateJob = null
        cardStateJob = viewModelScope.launch {
            storageExistHelper.invalidate(this, force = false)
            val latestVersionAsync = async {
                val result = runCatching { downloaderApi.getLatestVersion() }
                verbose { "latestVersionAsyncResult: $result" }
                return@async result
            }
            combine(
                flipperVersionProviderApi.getCurrentFlipperVersion(),
                storageExistHelper.isExternalStorageExist(),
                updateOfferHelper.isUpdateRequire(fStorageFeatureApi),
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
