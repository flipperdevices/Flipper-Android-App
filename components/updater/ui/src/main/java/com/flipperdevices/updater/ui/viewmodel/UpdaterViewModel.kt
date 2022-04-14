package com.flipperdevices.updater.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.ui.di.UpdaterComponent
import com.flipperdevices.updater.ui.model.UpdateCardState
import com.flipperdevices.updater.ui.utils.isGreaterThan
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdaterViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val updateCardState = MutableStateFlow<UpdateCardState>(
        UpdateCardState.InProgress
    )

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

    fun getUpdateCardState(): StateFlow<UpdateCardState> = updateCardState

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch {
            val latestVersionAsync = async { downloaderApi.getLatestVersion() }

            flipperVersionProviderApi
                .getCurrentFlipperVersion(viewModelScope, serviceApi)
                .collectLatest { flipperFirmwareVersion ->
                    if (flipperFirmwareVersion == null) {
                        return@collectLatest
                    }
                    val latestVersionFromNetwork =
                        latestVersionAsync.await()[flipperFirmwareVersion.channel]
                    if (latestVersionFromNetwork == null) {
                        updateCardState.emit(UpdateCardState.NoUpdate(flipperFirmwareVersion))
                        return@collectLatest
                    }
                    val isUpdateAvailable = latestVersionFromNetwork.version
                        .isGreaterThan(flipperFirmwareVersion) ?: true

                    if (isUpdateAvailable) {
                        updateCardState.emit(
                            UpdateCardState.UpdateAvailable(
                                lastVersion = latestVersionFromNetwork.version,
                                updaterDist = latestVersionFromNetwork.updaterFile
                            )
                        )
                    } else updateCardState.emit(UpdateCardState.NoUpdate(flipperFirmwareVersion))
                }
        }
    }
}
