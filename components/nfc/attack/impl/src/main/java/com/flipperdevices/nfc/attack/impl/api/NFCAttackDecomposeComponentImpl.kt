package com.flipperdevices.nfc.attack.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.nfc.attack.api.NFCAttackDecomposeComponent
import com.flipperdevices.nfc.attack.impl.model.NFCAttackNavigationConfig
import com.flipperdevices.nfc.mfkey32.api.MfKey32DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, NFCAttackDecomposeComponent.Factory::class)
class NFCAttackDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted deeplink: Deeplink.BottomBar.HubTab.OpenMfKey?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val nfcAttackFactory: NFCAttackScreenDecomposeComponentImpl.Factory,
    private val mfKey32Factory: MfKey32DecomposeComponent.Factory
) : NFCAttackDecomposeComponent<NFCAttackNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<NFCAttackNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = NFCAttackNavigationConfig.serializer(),
        initialStack = {
            if (deeplink == null) {
                listOf(NFCAttackNavigationConfig.NFCAttack)
            } else {
                listOf(NFCAttackNavigationConfig.NFCAttack, NFCAttackNavigationConfig.MfKey32)
            }
        },
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: NFCAttackNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        NFCAttackNavigationConfig.MfKey32 -> mfKey32Factory(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        NFCAttackNavigationConfig.NFCAttack -> nfcAttackFactory(
            componentContext = componentContext,
            navigation = navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }
}
