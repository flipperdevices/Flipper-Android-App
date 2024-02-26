package com.flipperdevices.settings.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.settings.impl.composable.ComposableSettings
import com.flipperdevices.settings.impl.model.SettingsNavigationConfig
import com.flipperdevices.settings.impl.viewmodels.DebugViewModel
import com.flipperdevices.settings.impl.viewmodels.NotificationViewModel
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import com.flipperdevices.settings.impl.viewmodels.VersionViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

@Suppress("LongParameterList")
class MainScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<SettingsNavigationConfig>,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val notificationViewModelProvider: Provider<NotificationViewModel>,
    private val settingsViewModelProvider: Provider<SettingsViewModel>,
    private val debugViewModelProvider: Provider<DebugViewModel>,
    private val versionViewModelProvider: Provider<VersionViewModel>
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val settingsViewModel = viewModelWithFactory(key = null) {
            settingsViewModelProvider.get()
        }
        val notificationViewModel = viewModelWithFactory(key = null) {
            notificationViewModelProvider.get()
        }
        val debugViewModel = viewModelWithFactory(key = null) {
            debugViewModelProvider.get()
        }
        val versionViewModel = viewModelWithFactory(key = null) {
            versionViewModelProvider.get()
        }

        ComposableSettings(
            settingsViewModel = settingsViewModel,
            notificationViewModel = notificationViewModel,
            versionViewModel = versionViewModel,
            onBack = onBack::invoke,
            onOpen = { navigation.pushToFront(it) },
            debugViewModel = debugViewModel,
            onDebugAction = { debugViewModel.onAction(it, navigation) },
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<SettingsNavigationConfig>,
            onBack: DecomposeOnBackParameter
        ): MainScreenDecomposeComponent
    }
}
