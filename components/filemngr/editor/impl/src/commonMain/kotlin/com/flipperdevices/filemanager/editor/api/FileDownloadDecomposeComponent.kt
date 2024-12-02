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
import com.flipperdevices.filemanager.editor.composable.download.UploadingComposable
import com.flipperdevices.filemanager.editor.viewmodel.DownloadViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
                .onEach {
                    onDownloaded.invoke()
                }.launchIn(this)
        }
        val state by downloadViewModel.state.collectAsState()

        when (val localState = state) {
            DownloadViewModel.State.CouldNotDownload -> {
                Box(Modifier.fillMaxSize().background(Color.Red))
            }

            DownloadViewModel.State.Downloaded -> {
                Box(Modifier.fillMaxSize().background(Color.Green))
            }

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
                Box(Modifier.fillMaxSize().background(Color.Cyan))
            }

            DownloadViewModel.State.Unsupported -> {
                Box(Modifier.fillMaxSize().background(Color.Yellow))
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
