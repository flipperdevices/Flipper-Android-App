package com.flipperdevices.filemanager.upload.impl.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.filemanager.upload.api.UploadDecomposeComponent
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent
import com.flipperdevices.filemanager.upload.impl.composable.PickFilesEffect
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path

@ContributesAssistedFactory(AppGraph::class, UploadDecomposeComponent.Factory::class)
class UploadDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onFinish: () -> Unit,
    private val deepLinkParser: DeepLinkParser,
    uploaderDecomposeComponentFactory: UploaderDecomposeComponent.Factory
) : UploadDecomposeComponent(componentContext) {
    private val uploaderDecomposeComponent = uploaderDecomposeComponentFactory.invoke(
        componentContext = childContext("uploaderDecomposeComponent_$path")
    )

    private val backCallback = BackCallback {
        uploaderDecomposeComponent.onCancel()
    }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    override fun Render() {
        val state by uploaderDecomposeComponent.state.collectAsState()
        val speedState by uploaderDecomposeComponent.speedState.collectAsState(null)

        LaunchedEffect(uploaderDecomposeComponent) {
            uploaderDecomposeComponent.state
                .filter { it !is UploaderDecomposeComponent.State.Uploading }
                .filter { it !is UploaderDecomposeComponent.State.Pending }
                .onEach { onFinish.invoke() }
                .launchIn(this)
        }
        PickFilesEffect(
            deepLinkParser = deepLinkParser,
            onBack = uploaderDecomposeComponent::onCancel,
            onContentsReady = { deeplinkContents ->
                uploaderDecomposeComponent.tryUpload(
                    folderPath = path,
                    contents = deeplinkContents
                )
            }
        )
        uploaderDecomposeComponent.Render(
            state = state,
            speedState = speedState,
            onCancel = uploaderDecomposeComponent::onCancel,
            modifier = Modifier
                .fillMaxSize()
                .background(LocalPalletV2.current.surface.backgroundMain.body)
                .navigationBarsPadding()
                .systemBarsPadding(),
        )
    }
}
