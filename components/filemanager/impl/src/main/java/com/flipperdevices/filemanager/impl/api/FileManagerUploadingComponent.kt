package com.flipperdevices.filemanager.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerUploadedScreen
import com.flipperdevices.filemanager.impl.model.FileManagerNavigationConfig
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.filemanager.impl.viewmodels.ReceiveViewModel
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
        val shareState by uploaderViewModel.getReceiveState().collectAsState()
        ComposableFileManagerUploadedScreen(
            fileManagerState = fileManagerState,
            shareState = shareState,
            onBack = onBack::invoke
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
