package com.flipperdevices.newfilemanager.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.newfilemanager.impl.composable.ComposableFileManagerScreen
import com.flipperdevices.newfilemanager.impl.model.FileManagerNavigationConfig
import com.flipperdevices.newfilemanager.impl.model.ShareFile
import com.flipperdevices.newfilemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class FileManagerListingComponent @AssistedInject constructor(
    private val deepLinkParser: DeepLinkParser,
    private val fileManagerViewModelFactory: FileManagerViewModel.Factory,
    @Assisted componentContext: ComponentContext,
    @Assisted val config: FileManagerNavigationConfig.Screen,
    @Assisted val navigation: StackNavigation<FileManagerNavigationConfig>
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        ComposableFileManagerScreen(
            fileManagerViewModel = viewModelWithFactory(config.path) {
                fileManagerViewModelFactory(config.path)
            },
            deepLinkParser = deepLinkParser,
            onOpenFolder = {
                navigation.pushToFront(FileManagerNavigationConfig.Screen(it.path))
            },
            onOpenEditor = {
                navigation.pushToFront(FileManagerNavigationConfig.Editing(ShareFile(it)))
            },
            onDownloadAndShareFile = {
                val shareFile = ShareFile(it)
                navigation.pushToFront(
                    FileManagerNavigationConfig.Download(
                        path = File(shareFile.flipperFilePath).absoluteFile.parent ?: "/",
                        shareFile = shareFile
                    )
                )
            },
            onUploadFile = { path, content ->
                navigation.pushToFront(
                    FileManagerNavigationConfig.Uploading(
                        path = path,
                        deeplinkContent = content
                    )
                )
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            config: FileManagerNavigationConfig.Screen,
            navigation: StackNavigation<FileManagerNavigationConfig>
        ): FileManagerListingComponent
    }
}
