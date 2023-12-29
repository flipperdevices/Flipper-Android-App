package com.flipperdevices.nfc.attack.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

interface NFCAttackDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.HubTab.OpenMfKey?,
            onBack: DecomposeOnBackParameter
        ): NFCAttackDecomposeComponent
    }
}
