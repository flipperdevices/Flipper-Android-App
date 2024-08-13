package com.flipperdevices.remotecontrols.impl.createcontrol.decompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.api.CreateControlDecomposeComponent
import com.flipperdevices.remotecontrols.impl.createcontrol.composable.CreateControlComposable
import com.flipperdevices.remotecontrols.impl.createcontrol.viewmodel.SaveRemoteControlViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, CreateControlDecomposeComponent.Factory::class)
class CreateControlDecomposeComponentImpl @AssistedInject constructor(
    @Assisted private val componentContext: ComponentContext,
    @Assisted private val savedKey: FlipperKeyPath,
    @Assisted private val originalKey: NotSavedFlipperKey,
    @Assisted private val onFinished: (FlipperKeyPath) -> Unit,
    @Assisted private val onFailed: () -> Unit,
    private val saveRemoteControlViewModelFactory: Provider<SaveRemoteControlViewModel>
) : CreateControlDecomposeComponent(componentContext) {

    @Composable
    override fun Render() {
        val saveRemoteControlViewModel = viewModelWithFactory(
            key = null,
            factory = { saveRemoteControlViewModelFactory.get() }
        )
        LaunchedEffect(saveRemoteControlViewModel) {
            saveRemoteControlViewModel.state
                .onEach {
                    when (it) {
                        is SaveRemoteControlViewModel.State.Finished -> onFinished.invoke(it.keyPath)
                        SaveRemoteControlViewModel.State.CouldNotModifyFiles,
                        SaveRemoteControlViewModel.State.KeyNotFound -> onFailed.invoke()

                        SaveRemoteControlViewModel.State.Pending,
                        SaveRemoteControlViewModel.State.Updating -> Unit
                    }
                }.launchIn(this)
            saveRemoteControlViewModel.moveAndUpdate(
                savedKeyPath = savedKey,
                originalKey = originalKey,
            )
        }
        CreateControlComposable()
    }
}
