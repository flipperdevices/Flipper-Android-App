package com.flipperdevices.wearable.emulate.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.wearable.core.ui.components.ComposableWearOsScrollableColumn
import com.flipperdevices.wearable.emulate.api.WearEmulateDecomposeComponent
import com.flipperdevices.wearable.emulate.impl.composable.ComposableWearEmulate
import com.flipperdevices.wearable.emulate.impl.viewmodel.WearEmulateViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, WearEmulateDecomposeComponent.Factory::class)
class WearEmulateDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val flipperKeyPath: FlipperKeyPath,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val keyEmulateUiApi: KeyEmulateUiApi,
    private val wearEmulateViewModelFactory: WearEmulateViewModel.Factory
) : WearEmulateDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val emulateViewModel = viewModelWithFactory(key = flipperKeyPath.toString()) {
            wearEmulateViewModelFactory(flipperKeyPath)
        }
        ComposableWearOsScrollableColumn(
            content = {
                ComposableWearEmulate(
                    keyEmulateUiApi = keyEmulateUiApi,
                    onBack = onBack::invoke,
                    emulateViewModel = emulateViewModel
                )
            }
        )
    }
}
