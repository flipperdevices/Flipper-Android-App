package com.flipperdevices.share.receive.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.api.KeyReceiveDecomposeComponent
import com.flipperdevices.share.receive.composable.ComposableKeyReceive
import com.flipperdevices.share.receive.viewmodels.KeyReceiveViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class KeyReceiveDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val deeplink: Deeplink.RootLevel.SaveKey,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val keyScreenApi: KeyScreenApi,
    private val keyReceiveViewModelFactory: KeyReceiveViewModel.Factory
) : KeyReceiveDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val viewModel: KeyReceiveViewModel = viewModelWithFactory(key = deeplink.toString()) {
            keyReceiveViewModelFactory(deeplink)
        }
        val state by viewModel.getState().collectAsState()
        ComposableKeyReceive(
            keyScreenApi = keyScreenApi,
            onCancel = {
                viewModel.onFinish()
                onBack()
            },
            state = state,
            onRetry = viewModel::onRetry,
            onSave = viewModel::onSave
        )
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, KeyReceiveDecomposeComponent.Factory::class)
    fun interface Factory : KeyReceiveDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.RootLevel.SaveKey,
            onBack: DecomposeOnBackParameter
        ): KeyReceiveDecomposeComponentImpl
    }
}
