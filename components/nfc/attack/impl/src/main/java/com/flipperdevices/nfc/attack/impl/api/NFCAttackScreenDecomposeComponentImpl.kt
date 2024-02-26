package com.flipperdevices.nfc.attack.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.nfc.attack.impl.composable.ComposableNfcAttack
import com.flipperdevices.nfc.attack.impl.model.NFCAttackNavigationConfig
import com.flipperdevices.nfc.attack.impl.viewmodel.NfcAttackViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class NFCAttackScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<NFCAttackNavigationConfig>,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val nfcAttackViewModelProvider: Provider<NfcAttackViewModel>
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val nfcAttackViewModel = viewModelWithFactory(null) {
            nfcAttackViewModelProvider.get()
        }
        val hasMfKey32Notification by nfcAttackViewModel.hasMfKey32Notification().collectAsState()

        ComposableNfcAttack(
            onOpenMfKey32 = {
                navigation.pushToFront(NFCAttackNavigationConfig.MfKey32)
            },
            onBack = onBack::invoke,
            hasMfKey32Notification = hasMfKey32Notification
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<NFCAttackNavigationConfig>,
            onBack: DecomposeOnBackParameter
        ): NFCAttackScreenDecomposeComponentImpl
    }
}
