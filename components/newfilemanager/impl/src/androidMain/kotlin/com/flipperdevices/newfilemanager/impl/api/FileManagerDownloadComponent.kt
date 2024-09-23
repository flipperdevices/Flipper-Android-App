package com.flipperdevices.newfilemanager.impl.api

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.newfilemanager.impl.composable.ComposableFileManagerDownloadScreen
import com.flipperdevices.newfilemanager.impl.model.FileManagerNavigationConfig
import com.flipperdevices.newfilemanager.impl.model.ShareState
import com.flipperdevices.newfilemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.newfilemanager.impl.viewmodels.ShareViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import flipperapp.components.newfilemanager.impl.generated.resources.Res
import flipperapp.components.newfilemanager.impl.generated.resources.filemanager_error
import org.jetbrains.compose.resources.stringResource

class FileManagerDownloadComponent @AssistedInject constructor(
    private val fileManagerViewModelFactory: FileManagerViewModel.Factory,
    private val shareModelFactory: ShareViewModel.Factory,
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: FileManagerNavigationConfig.Download,
    @Assisted private val onBack: DecomposeOnBackParameter
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val fileManagerViewModel = viewModelWithFactory(key = config.path) {
            fileManagerViewModelFactory(config.path)
        }
        val shareViewModel = viewModelWithFactory(key = config.shareFile.toString()) {
            shareModelFactory(config.shareFile)
        }
        val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()
        val shareState by shareViewModel.getShareState().collectAsState()
        val speedState by shareViewModel.getSpeedState().collectAsState()

        shareState.let { shareStateLocal ->
            when (shareStateLocal) {
                ShareState.Error -> Text(stringResource(Res.string.filemanager_error))
                is ShareState.Ready -> ComposableFileManagerDownloadScreen(
                    fileManagerState = fileManagerState,
                    shareState = shareStateLocal,
                    onBack = onBack::invoke,
                    speedState = speedState
                )
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            config: FileManagerNavigationConfig.Download,
            onBack: DecomposeOnBackParameter
        ): FileManagerDownloadComponent
    }
}
