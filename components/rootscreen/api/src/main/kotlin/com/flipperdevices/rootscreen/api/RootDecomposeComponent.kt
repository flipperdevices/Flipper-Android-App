package com.flipperdevices.rootscreen.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

interface RootDecomposeComponent : RootNavigationInterface {
    @Composable
    fun Render(modifier: Modifier)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): RootDecomposeComponent
    }
}
