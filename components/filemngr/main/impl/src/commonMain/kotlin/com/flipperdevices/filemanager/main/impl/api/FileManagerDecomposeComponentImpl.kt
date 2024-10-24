package com.flipperdevices.filemanager.main.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.editor.api.FileManagerEditorDecomposeComponent
import com.flipperdevices.filemanager.listing.api.FilesDecomposeComponent
import com.flipperdevices.filemanager.main.api.FileManagerDecomposeComponent
import com.flipperdevices.filemanager.main.impl.model.FileManagerNavigationConfig
import com.flipperdevices.filemanager.search.api.SearchDecomposeComponent
import com.flipperdevices.filemanager.upload.api.UploadDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, FileManagerDecomposeComponent.Factory::class)
class FileManagerDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val filesDecomposeComponentFactory: FilesDecomposeComponent.Factory,
    private val uploadDecomposeComponentFactory: UploadDecomposeComponent.Factory,
    private val searchDecomposeComponentFactory: SearchDecomposeComponent.Factory,
    private val editorDecomposeComponentFactory: FileManagerEditorDecomposeComponent.Factory,
) : FileManagerDecomposeComponent<FileManagerNavigationConfig>(),
    ComponentContext by componentContext {

    override val stack = childStack(
        source = navigation,
        serializer = FileManagerNavigationConfig.serializer(),
        initialConfiguration = FileManagerNavigationConfig.DefaultFileTree,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FileManagerNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is FileManagerNavigationConfig.FileTree -> {
            filesDecomposeComponentFactory.invoke(
                componentContext = componentContext,
                path = config.path,
                onBack = { navigation.popOr(onBack::invoke) },
                pathChangedCallback = {
                    navigation.replaceCurrent(FileManagerNavigationConfig.FileTree(it))
                },
                fileSelectedCallback = {
                    navigation.pushNew(FileManagerNavigationConfig.Edit(it))
                },
                uploadCallback = { navigation.pushNew(FileManagerNavigationConfig.Upload(config.path)) },
                searchCallback = { navigation.pushNew(FileManagerNavigationConfig.Search(config.path)) },
            )
        }

        is FileManagerNavigationConfig.Upload -> {
            uploadDecomposeComponentFactory.invoke(
                componentContext = componentContext,
                path = config.path,
                onFinish = navigation::pop
            )
        }

        is FileManagerNavigationConfig.Search -> {
            searchDecomposeComponentFactory.invoke(
                componentContext = componentContext,
                path = config.path,
                onBack = navigation::pop,
                onFolderSelect = { navigation.replaceAll(FileManagerNavigationConfig.FileTree(it)) }
            )
        }

        is FileManagerNavigationConfig.Edit -> {
            editorDecomposeComponentFactory.invoke(
                componentContext = componentContext,
                path = config.path,
                onBack = { navigation.popOr(onBack::invoke) },
            )
        }
    }
}
