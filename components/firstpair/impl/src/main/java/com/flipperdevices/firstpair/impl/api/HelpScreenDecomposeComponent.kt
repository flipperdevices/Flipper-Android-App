package com.flipperdevices.firstpair.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.firstpair.impl.composable.help.ComposableHelp
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HelpScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter
) : ComponentContext by componentContext, DecomposeComponent {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        ComposableHelp(onBack = onBack::invoke)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            @Assisted onBack: DecomposeOnBackParameter
        ): HelpScreenDecomposeComponent
    }
}