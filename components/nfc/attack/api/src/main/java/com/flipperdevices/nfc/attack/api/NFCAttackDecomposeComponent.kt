package com.flipperdevices.nfc.attack.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent

interface NFCAttackDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): NFCAttackDecomposeComponent
    }
}
