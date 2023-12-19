package com.flipperdevices.faphub.fapscreen.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.fapscreen.impl.composable.ComposableFapScreen
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenNavigationConfig
import com.flipperdevices.faphub.fapscreen.impl.viewmodel.FapScreenViewModel
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.flipperdevices.faphub.uninstallbutton.api.FapUninstallApi
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("LongParameterList")
class ScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val id: String,
    @Assisted private val navigation: StackNavigation<FapScreenNavigationConfig>,
    private val fapScreenViewModelFactory: FapScreenViewModel.Factory,
    private val installationUIApi: FapInstallationUIApi,
    private val bottomBarApi: BottomNavigationHandleDeeplink,
    private val uninstallApi: FapUninstallApi,
    private val errorsRenderer: FapHubComposableErrorsRenderer
) : DecomposeComponent, ComponentContext by componentContext {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val fapScreenViewModel = viewModelWithFactory(key = id) {
            fapScreenViewModelFactory(id)
        }
        val loadingState by fapScreenViewModel.getLoadingState().collectAsState()
        val controlState by fapScreenViewModel.getControlState().collectAsState()

        ComposableFapScreen(
            onBack = navigation::pop,
            installationButton = { fapItem, modifier ->
                installationUIApi.ComposableButton(
                    config = fapItem?.toFapButtonConfig(),
                    modifier = modifier,
                    fapButtonSize = FapButtonSize.LARGE
                )
            },
            onOpenDeviceTab = { bottomBarApi.onChangeTab(BottomBarTab.DEVICE, force = true) },
            uninstallButton = { modifier, fapItem ->
                uninstallApi.ComposableFapUninstallButton(
                    modifier = modifier,
                    fapItem = fapItem
                )
            },
            errorsRenderer = errorsRenderer,
            loadingState = loadingState,
            controlState = controlState,
            onRefresh = fapScreenViewModel::onRefresh,
            onPressHide = { fapScreenViewModel.onPressHide(it.isHidden, navigation::pop) },
            onOpenReport = {
                navigation.push(fapScreenViewModel.getReportAppNavigationConfig(it))
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            id: String,
            navigation: StackNavigation<FapScreenNavigationConfig>
        ): ScreenDecomposeComponentImpl
    }
}
