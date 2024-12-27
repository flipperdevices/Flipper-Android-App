package com.flipperdevices.bridge.connection.screens.device

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.pushNew
import com.flipperdevices.bridge.connection.screens.device.composable.FCurrentDeviceComposable
import com.flipperdevices.bridge.connection.screens.device.composable.FDeviceDropdownComposable
import com.flipperdevices.bridge.connection.screens.device.composable.FPingComposable
import com.flipperdevices.bridge.connection.screens.device.viewmodel.FCurrentDeviceViewModel
import com.flipperdevices.bridge.connection.screens.device.viewmodel.FDevicesViewModel
import com.flipperdevices.bridge.connection.screens.device.viewmodel.PingViewModel
import com.flipperdevices.bridge.connection.screens.models.ConnectionRootConfig
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactoryWithoutRemember
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class ConnectionDeviceScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigator<ConnectionRootConfig>,
    private val devicesViewModelProvider: Provider<FDevicesViewModel>,
    private val currentDeviceViewModelProvider: Provider<FCurrentDeviceViewModel>,
    private val pingViewModelProvider: Provider<PingViewModel>
) : ScreenDecomposeComponent(componentContext) {
    private val devicesViewModel = viewModelWithFactoryWithoutRemember(null) {
        devicesViewModelProvider.get()
    }
    private val currentDeviceViewModel = viewModelWithFactoryWithoutRemember(null) {
        currentDeviceViewModelProvider.get()
    }
    private val pingViewModel = viewModelWithFactoryWithoutRemember(null) {
        pingViewModelProvider.get()
    }

    @Composable
    override fun Render() {
        Column {
            val deviceState by devicesViewModel.getState().collectAsState()
            FDeviceDropdownComposable(
                devicesState = deviceState,
                onDeviceSelect = devicesViewModel::onSelectDevice,
                onOpenSearch = { navigation.pushNew(ConnectionRootConfig.Search) },
                onDisconnect = devicesViewModel::onDisconnect
            )
            val currentDevice by currentDeviceViewModel.getState().collectAsState()
            FCurrentDeviceComposable(currentDevice)
            val logs by pingViewModel.getLogLinesState().collectAsState()
            FPingComposable(
                logs = logs,
                onSendPing = pingViewModel::sendPing,
                invalidateRpcInfo = pingViewModel::invalidateRpcInfo,
                onOpenFM = {
                    navigation.pushNew(ConnectionRootConfig.FileManager)
                }
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted componentContext: ComponentContext,
            @Assisted navigation: StackNavigator<ConnectionRootConfig>
        ): ConnectionDeviceScreenDecomposeComponent
    }
}
