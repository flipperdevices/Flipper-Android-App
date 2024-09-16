package com.flipperdevices.toolstab.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.toolstab.impl.composable.ComposableHub
import com.flipperdevices.toolstab.impl.model.ToolsNavigationConfig
import com.flipperdevices.toolstab.impl.viewmodel.DebugSettingsViewModel
import com.flipperdevices.toolstab.impl.viewmodel.ToolsNotificationViewModel
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class ToolsMainScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<ToolsNavigationConfig>,
    private val toolsNotificationViewModelProvider: Provider<ToolsNotificationViewModel>,
    private val debugSettingsViewModelProvider: Provider<DebugSettingsViewModel>,
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val nfcAttackViewModel = viewModelWithFactory(key = null) {
            toolsNotificationViewModelProvider.get()
        }
        val debugSettingsViewModel = viewModelWithFactory(key = null) {
            debugSettingsViewModelProvider.get()
        }
        val hasNotification by nfcAttackViewModel.hasNotificationStateFlow.collectAsState()
        val showRemoteControls by debugSettingsViewModel.showRemoteControls.collectAsState()

        ComposableHub(
            hasNotification = hasNotification,
            showRemoteControls = showRemoteControls,
            onOpenMfKey32 = {
                navigation.pushToFront(ToolsNavigationConfig.MfKey32)
            },
            onOpenRemoteControls = {
                navigation.pushToFront(ToolsNavigationConfig.RemoteControls)
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<ToolsNavigationConfig>
        ): ToolsMainScreenDecomposeComponentImpl
    }
}
