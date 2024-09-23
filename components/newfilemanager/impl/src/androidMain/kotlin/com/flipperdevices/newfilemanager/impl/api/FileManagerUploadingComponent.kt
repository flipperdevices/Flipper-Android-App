package com.flipperdevices.newfilemanager.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.newfilemanager.impl.composable.ComposableFileManagerUploadedScreen
import com.flipperdevices.newfilemanager.impl.model.FileManagerNavigationConfig
import com.flipperdevices.newfilemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.newfilemanager.impl.viewmodels.ReceiveViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileManagerUploadingComponent @AssistedInject constructor(
    private val fileManagerViewModelFactory: FileManagerViewModel.Factory,
    private val receiveViewModelFactory: ReceiveViewModel.Factory,
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: FileManagerNavigationConfig.Uploading,
    @Assisted private val onBack: DecomposeOnBackParameter
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val fileManagerViewModel = viewModelWithFactory(key = config.path) {
            fileManagerViewModelFactory(config.path)
        }
        val uploaderViewModel = viewModelWithFactory(key = config.toString()) {
            receiveViewModelFactory(config.deeplinkContent, config.path)
        }
        val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()
        val shareState by uploaderViewModel.getShareState().collectAsState()
        val speedState by uploaderViewModel.getSpeedState().collectAsState()

        ComposableFileManagerUploadedScreen(
            fileManagerState = fileManagerState,
            shareState = shareState,
            onBack = onBack::invoke,
            speedState = speedState
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            config: FileManagerNavigationConfig.Uploading,
            onBack: DecomposeOnBackParameter
        ): FileManagerUploadingComponent
    }
}
