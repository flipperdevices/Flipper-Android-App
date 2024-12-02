package com.flipperdevices.filemanager.editor.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.filemanager.editor.composable.download.UploadingComposable
import com.flipperdevices.filemanager.editor.viewmodel.UploadFileViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okio.Path
import flipperapp.components.filemngr.editor.impl.generated.resources.Res as FME
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_status_uploading
import org.jetbrains.compose.resources.stringResource

@Suppress("LongParameterList")
class UploadFileDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
    @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
    @Assisted private val onProgress: (ListingItem) -> Unit,
    @Assisted private val onFinished: () -> Unit,
    uploadFileViewModelFactory: UploadFileViewModel.Factory
) : ScreenDecomposeComponent(componentContext) {
    private val uploadFileViewModel = instanceKeeper.getOrCreate {
        uploadFileViewModelFactory.invoke(
            fullPathOnFlipper = fullPathOnFlipper,
            fullPathOnDevice = fullPathOnDevice,
        )
    }

    @Composable
    override fun Render() {
        LaunchedEffect(uploadFileViewModel) {
            uploadFileViewModel.state
                .filterIsInstance<UploadFileViewModel.State.Uploading>()
                .onEach { state ->
                    val listingItem = ListingItem(
                        fileName = state.fullPathOnFlipper.name,
                        fileType = FileType.FILE,
                        size = state.uploaded
                    )
                    onProgress.invoke(listingItem)
                }.launchIn(this)

            uploadFileViewModel.state
                .filterIsInstance<UploadFileViewModel.State.Saved>()
                .onEach { onFinished.invoke() }
                .launchIn(this)
        }

        val state by uploadFileViewModel.state.collectAsState()

        when (val localState = state) {
            UploadFileViewModel.State.Error -> {
                Box(Modifier.fillMaxSize().background(Color.Red))
            }

            is UploadFileViewModel.State.Saved -> {
                Box(Modifier.fillMaxSize().background(Color.Green))
            }

            UploadFileViewModel.State.Unsupported -> {
                Box(Modifier.fillMaxSize().background(Color.Yellow))
            }

            is UploadFileViewModel.State.Uploading -> {
                UploadingComposable(
                    progress = localState.progress,
                    fullPathOnFlipper = localState.fullPathOnFlipper,
                    current = localState.uploaded,
                    max = localState.total,
                    speed = uploadFileViewModel.speedState.collectAsState().value,
                    onCancel = onBack::invoke,
                    modifier = Modifier,
                    title = stringResource(FME.string.fme_status_uploading)
                )
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
            @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
            onProgress: (ListingItem) -> Unit,
            onFinished: () -> Unit,
        ): UploadFileDecomposeComponent
    }
}
