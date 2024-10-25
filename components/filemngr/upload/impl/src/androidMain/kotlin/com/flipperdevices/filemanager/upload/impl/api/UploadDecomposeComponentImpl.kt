package com.flipperdevices.filemanager.upload.impl.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.filemanager.upload.api.UploadDecomposeComponent
import com.flipperdevices.filemanager.upload.impl.composable.PickFilesEffect
import com.flipperdevices.filemanager.upload.impl.composable.UploadingComposable
import com.flipperdevices.filemanager.upload.impl.viewmodel.UploadViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, UploadDecomposeComponent.Factory::class)
class UploadDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onFinish: () -> Unit,
    private val deepLinkParser: DeepLinkParser,
    private val uploadViewModelFactory: Provider<UploadViewModel>
) : UploadDecomposeComponent(componentContext) {
    private val uploadViewModel = instanceKeeper.getOrCreate {
        uploadViewModelFactory.get()
    }

    private val backCallback = BackCallback {
        uploadViewModel.onCancel()
    }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    override fun Render() {
        LaunchedEffect(uploadViewModel) {
            uploadViewModel.state
                .filter { it !is UploadViewModel.State.Uploading }
                .filter { it !is UploadViewModel.State.Pending }
                .onEach { onFinish.invoke() }
                .launchIn(this)
        }
        PickFilesEffect(
            deepLinkParser = deepLinkParser,
            onBack = uploadViewModel::onCancel,
            onContentsReady = { deeplinkContents ->
                uploadViewModel.tryUpload(
                    folderPath = path,
                    contents = deeplinkContents
                )
            }
        )
        val state by uploadViewModel.state.collectAsState()
        val speedState by uploadViewModel.speedState.collectAsState(null)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .systemBarsPadding(),
            content = {
                when (val localState = state) {
                    is UploadViewModel.State.Uploading -> {
                        UploadingComposable(
                            state = localState,
                            speed = speedState,
                            onCancel = uploadViewModel::onCancel,
                        )
                    }

                    UploadViewModel.State.Cancelled,
                    UploadViewModel.State.Error,
                    UploadViewModel.State.Pending,
                    UploadViewModel.State.Uploaded -> Unit
                }
            }
        )
    }
}
