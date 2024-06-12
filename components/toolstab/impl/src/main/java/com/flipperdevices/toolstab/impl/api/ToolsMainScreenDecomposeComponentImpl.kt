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
import com.flipperdevices.toolstab.impl.viewmodel.NfcAttackViewModel
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class ToolsMainScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<ToolsNavigationConfig>,
    private val nfcAttackViewModelProvider: Provider<NfcAttackViewModel>
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val nfcAttackViewModel = viewModelWithFactory(key = null) {
            nfcAttackViewModelProvider.get()
        }
        val notificationCount by nfcAttackViewModel.getNfcAttackNotificationCountState()
            .collectAsState()

        ComposableHub(
            notificationCount = notificationCount,
            onOpenAttack = {
                navigation.pushToFront(ToolsNavigationConfig.NfcAttack(null))
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
