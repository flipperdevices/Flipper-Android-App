package com.flipperdevices.remotecontrols.impl.createcontrol.decompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.impl.createcontrol.composable.CreateControlComposable
import com.flipperdevices.remotecontrols.impl.createcontrol.viewmodel.SaveRemoteControlViewModel
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Provider

class CreateControlDecomposeComponentImpl @AssistedInject constructor(
    @Assisted private val componentContext: ComponentContext,
    @Assisted private val savedKey: FlipperKeyPath,
    @Assisted private val originalKey: NotSavedFlipperKey,
    @Assisted private val onFinished: (FlipperKeyPath) -> Unit,
    @Assisted private val onFailed: () -> Unit,
    private val saveRemoteControlViewModelFactory: Provider<SaveRemoteControlViewModel>
) : ScreenDecomposeComponent(componentContext) {

    @Composable
    override fun Render() {
        val saveRemoteControlViewModel = viewModelWithFactory(key = null) {
            saveRemoteControlViewModelFactory.get()
        }
        LaunchedEffect(saveRemoteControlViewModel) {
            saveRemoteControlViewModel.state
                .filterIsInstance<SaveRemoteControlViewModel.State.Finished>()
                .onEach { onFinished.invoke(it.keyPath) }
                .launchIn(this)
            saveRemoteControlViewModel.state
                .filterIsInstance<SaveRemoteControlViewModel.State.KeyNotFound>()
                .onEach { onFailed.invoke() }
                .launchIn(this)
            saveRemoteControlViewModel.moveAndUpdate(
                savedKeyPath = savedKey,
                originalKey = originalKey,
            )
        }
        CreateControlComposable()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            savedKey: FlipperKeyPath,
            originalKey: NotSavedFlipperKey,
            onFinished: (FlipperKeyPath) -> Unit,
            onFailed: () -> Unit
        ): CreateControlDecomposeComponentImpl
    }
}
