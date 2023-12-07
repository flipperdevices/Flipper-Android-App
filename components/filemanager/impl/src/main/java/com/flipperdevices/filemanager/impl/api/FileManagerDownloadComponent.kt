package com.flipperdevices.filemanager.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerDownloadScreen
import com.flipperdevices.filemanager.impl.model.FileManagerNavigationConfig
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.filemanager.impl.viewmodels.ShareViewModel
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileManagerDownloadComponent @AssistedInject constructor(
    private val fileManagerViewModelFactory: FileManagerViewModel.Factory,
    private val shareModelFactory: ShareViewModel.Factory,
    @Assisted componentContext: ComponentContext,
    @Assisted val config: FileManagerNavigationConfig.Download,
    @Assisted val navigation: StackNavigation<FileManagerNavigationConfig>
) : DecomposeComponent,
    ComponentContext by componentContext {
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
        ComposableFileManagerDownloadScreen(
            fileManagerState = fileManagerState,
            shareState = shareState,
            onBack = navigation::pop
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            config: FileManagerNavigationConfig.Download,
            navigation: StackNavigation<FileManagerNavigationConfig>
        ): FileManagerDownloadComponent
    }
}
