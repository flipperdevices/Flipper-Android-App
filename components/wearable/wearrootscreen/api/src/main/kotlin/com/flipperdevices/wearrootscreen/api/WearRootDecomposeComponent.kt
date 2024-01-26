package com.flipperdevices.wearrootscreen.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.wearrootscreen.model.WearRootConfig

interface WearRootDecomposeComponent {

    val stack: Value<ChildStack<WearRootConfig, DecomposeComponent>>

    fun onBack()
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): WearRootDecomposeComponent
    }
}
