package com.flipperdevices.filemanager.editor.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.filemanager.editor.composable.download.UploadingComposable
import com.flipperdevices.filemanager.editor.viewmodel.DownloadViewModel
import com.flipperdevices.filemanager.ui.components.error.ErrorContentComposable
import com.flipperdevices.filemanager.ui.components.error.UnknownErrorComposable
import com.flipperdevices.filemanager.ui.components.error.UnsupportedErrorComposable
import com.flipperdevices.filemanager.util.constant.FileManagerConstants
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_error_too_large_desc
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_error_too_large_title
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_status_downloading
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okio.Path
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.editor.impl.generated.resources.Res as FME

class FileDownloadDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
    @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
    @Assisted private val onDownloaded: () -> Unit,
    @Assisted private val onBack: DecomposeOnBackParameter,
    downloadViewModelFactory: DownloadViewModel.Factory
) : ScreenDecomposeComponent(componentContext) {
    private val downloadViewModel = instanceKeeper.getOrCreate {
        downloadViewModelFactory.invoke(
            fullPathOnFlipper = fullPathOnFlipper,
            fullPathOnDevice = fullPathOnDevice
        )
    }

    @Composable
    override fun Render() {
        LaunchedEffect(downloadViewModel) {
            downloadViewModel.state
                .filterIsInstance<DownloadViewModel.State.Downloaded>()
                .onEach { onDownloaded.invoke() }
                .launchIn(this)
        }
        val state by downloadViewModel.state.collectAsState()

        when (val localState = state) {
            DownloadViewModel.State.CouldNotDownload -> {
                UnknownErrorComposable()
            }

            // Screen closed
            DownloadViewModel.State.Downloaded -> Unit

            is DownloadViewModel.State.Downloading -> {
                UploadingComposable(
                    progress = localState.progress,
                    fullPathOnFlipper = localState.fullPathOnFlipper,
                    current = localState.downloaded,
                    max = localState.total,
                    speed = downloadViewModel.speedState.collectAsState().value,
                    onCancel = onBack::invoke,
                    modifier = Modifier,
                    title = stringResource(FME.string.fme_status_downloading)
                )
            }

            DownloadViewModel.State.TooLarge -> {
                ErrorContentComposable(
                    text = stringResource(FME.string.fme_error_too_large_title),
                    desc = stringResource(
                        FME.string.fme_error_too_large_desc,
                        FileManagerConstants.LIMITED_SIZE_BYTES.toFormattedSize()
                    )
                )
            }

            DownloadViewModel.State.Unsupported -> {
                UnsupportedErrorComposable()
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
            @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
            onBack: DecomposeOnBackParameter,
            onDownloaded: () -> Unit
        ): FileDownloadDecomposeComponent
    }
}
