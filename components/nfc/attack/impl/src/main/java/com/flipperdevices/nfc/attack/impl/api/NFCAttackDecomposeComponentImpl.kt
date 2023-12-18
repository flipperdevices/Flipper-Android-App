package com.flipperdevices.nfc.attack.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfc.attack.api.NFCAttackDecomposeComponent
import com.flipperdevices.nfc.attack.impl.model.NFCAttackNavigationConfig
import com.flipperdevices.nfc.mfkey32.api.MfKey32DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class NFCAttackDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val nfcAttackFactory: NFCAttackScreenDecomposeComponentImpl.Factory,
    private val mfKey32Factory: MfKey32DecomposeComponent.Factory
) : NFCAttackDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<NFCAttackNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = NFCAttackNavigationConfig.serializer(),
        initialConfiguration = NFCAttackNavigationConfig.NFCAttack,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: NFCAttackNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        NFCAttackNavigationConfig.MfKey32 -> mfKey32Factory(
            componentContext = componentContext,
            onBack = navigation::pop
        )

        NFCAttackNavigationConfig.NFCAttack -> nfcAttackFactory(
            componentContext = componentContext,
            navigation = navigation
        )
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val childStack by stack.subscribeAsState()

        Children(
            stack = childStack,
        ) {
            it.instance.Render()
        }
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, NFCAttackDecomposeComponent.Factory::class)
    interface Factory : NFCAttackDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext
        ): NFCAttackDecomposeComponentImpl
    }
}