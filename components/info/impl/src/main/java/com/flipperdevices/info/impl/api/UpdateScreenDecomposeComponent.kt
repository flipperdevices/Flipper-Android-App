package com.flipperdevices.info.impl.api

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.bottombar.handlers.ResetTabDecomposeHandler
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.info.impl.compose.screens.ComposableDeviceInfoScreen
import com.flipperdevices.info.impl.model.DeviceScreenNavigationConfig
import com.flipperdevices.info.impl.viewmodel.AlarmViewModel
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel
import com.flipperdevices.info.impl.viewmodel.FlipperColorViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.BasicInfoViewModel
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import com.flipperdevices.updater.api.UpdaterCardApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Provider

@Suppress("LongParameterList")
class UpdateScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?,
    @Assisted private val navigator: StackNavigator<DeviceScreenNavigationConfig>,
    private val updaterCardApi: UpdaterCardApi,
    private val deviceStatusViewModelProvider: Provider<DeviceStatusViewModel>,
    private val connectViewModelProvider: Provider<ConnectViewModel>,
    private val flipperColorProvider: Provider<FlipperColorViewModel>,
    private val firmwareUpdateViewModelProvider: Provider<FirmwareUpdateViewModel>,
    private val alarmViewModelProvider: Provider<AlarmViewModel>,
    private val basicInfoViewModelProvider: Provider<BasicInfoViewModel>
) : ScreenDecomposeComponent(componentContext), ResetTabDecomposeHandler {
    private val requestScrollToTopFlow = MutableStateFlow(false)

    @Suppress("NonSkippableComposable")
    @Composable
    override fun Render() {
        val rootNavigation = LocalRootNavigation.current
        val deviceStatusViewModel = viewModelWithFactory(key = null) {
            deviceStatusViewModelProvider.get()
        }
        val deviceStatus by deviceStatusViewModel.getState().collectAsState()
        val updateState by deviceStatusViewModel.getUpdateState().collectAsState()
        val connectViewModel = viewModelWithFactory(key = null) {
            connectViewModelProvider.get()
        }
        val flipperColorViewModel = viewModelWithFactory(key = null) {
            flipperColorProvider.get()
        }
        val flipperColor by flipperColorViewModel.getFlipperColor().collectAsState()
        val firmwareUpdateViewModel = viewModelWithFactory(key = null) {
            firmwareUpdateViewModelProvider.get()
        }
        val supportState by firmwareUpdateViewModel.getState().collectAsState()
        val alarmViewModel = viewModelWithFactory(key = null) {
            alarmViewModelProvider.get()
        }
        val basicInfoViewModel = viewModelWithFactory(key = null) {
            basicInfoViewModelProvider.get()
        }
        val basicInfo by basicInfoViewModel.getDeviceInfo().collectAsState()

        val scrollState = rememberScrollState()
        val requestScrollToTop by requestScrollToTopFlow.collectAsState()
        LaunchedEffect(requestScrollToTop) {
            if (requestScrollToTop) {
                scrollState.animateScrollTo(0)
                requestScrollToTopFlow.emit(false)
            }
        }

        ComposableDeviceInfoScreen(
            updaterCardApi = updaterCardApi,
            onOpenFullDeviceInfo = {
                navigator.pushToFront(DeviceScreenNavigationConfig.FullInfo)
            },
            onOpenOptions = {
                navigator.pushToFront(DeviceScreenNavigationConfig.Options)
            },
            onStartUpdateRequest = {
                rootNavigation.push(RootScreenConfig.UpdateScreen(it))
            },
            deeplink = deeplink,
            deviceStatus = deviceStatus,
            connectViewModel = connectViewModel,
            hardwareColor = flipperColor,
            supportedState = supportState,
            updateState = updateState,
            alarmOnFlipper = alarmViewModel::alarmOnFlipper,
            deviceInfo = basicInfo,
            scrollState = scrollState,
            componentContext = this
        )
    }

    override fun onResetTab() {
        requestScrollToTopFlow.update { true }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?,
            navigator: StackNavigator<DeviceScreenNavigationConfig>
        ): UpdateScreenDecomposeComponent
    }
}
