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
import com.flipperdevices.filemanager.transfer.api.TransferDecomposeComponent
import com.flipperdevices.filemanager.transfer.api.model.TransferType
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.findComponentByConfig
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
    private val searchDecomposeComponentFactory: SearchDecomposeComponent.Factory,
    private val editorDecomposeComponentFactory: FileManagerEditorDecomposeComponent.Factory,
    private val transferDecomposeComponentFactory: TransferDecomposeComponent.Factory
) : FileManagerDecomposeComponent<FileManagerNavigationConfig>(),
    ComponentContext by componentContext {

    override val stack = childStack(
        source = navigation,
        serializer = FileManagerNavigationConfig.serializer(),
        initialConfiguration = FileManagerNavigationConfig.DefaultFileTree,
        handleBackButton = true,
        childFactory = ::child,
    )

    @Suppress("LongMethod")
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
                moveToCallback = { fullPaths ->
                    navigation.pushNew(
                        FileManagerNavigationConfig.Transfer(
                            path = config.path,
                            transferType = TransferType.MOVE,
                            fullPathToMove = fullPaths
                        )
                    )
                },
                searchCallback = { navigation.pushNew(FileManagerNavigationConfig.Search(config.path)) },
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
                onFileChanged = { item ->
                    val component = stack.findComponentByConfig(
                        configClazz = FileManagerNavigationConfig.FileTree::class
                    ) as? FilesDecomposeComponent
                    component?.onFileChanged(item)
                }
            )
        }

        is FileManagerNavigationConfig.Transfer -> {
            transferDecomposeComponentFactory.invoke(
                componentContext = componentContext,
                param = TransferDecomposeComponent.Param(
                    path = config.path,
                    transferType = config.transferType,
                    fullPathToMove = config.fullPathToMove
                ),
                onBack = { navigation.popOr(onBack::invoke) },
                onMoved = { path ->
                    navigation.replaceAll(FileManagerNavigationConfig.FileTree(path))
                },
                onPathChange = { path ->
                    navigation.replaceCurrent(
                        FileManagerNavigationConfig.Transfer(
                            path = path,
                            transferType = config.transferType,
                            fullPathToMove = config.fullPathToMove
                        )
                    )
                }
            )
        }
    }
}
