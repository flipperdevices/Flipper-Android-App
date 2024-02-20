package com.flipperdevices.firstpair.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.firstpair.impl.composable.tos.ComposableTOS
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TOSScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onApply: () -> Unit,
    private val firstPairStorage: FirstPairStorage
) : ScreenDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        ComposableTOS(
            onApplyPress = {
                firstPairStorage.markTosPassed()
                onApply()
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onApply: () -> Unit,
        ): TOSScreenDecomposeComponent
    }
}
