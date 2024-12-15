package com.flipperdevices.filemanager.editor.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.editor.model.FileManagerEditorConfiguration
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, FileManagerEditorDecomposeComponent.Factory::class)
class FileManagerEditorDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onFileChanged: (ListingItem) -> Unit,
    fileDownloadDecomposeComponentFactory: FileDownloadDecomposeComponent.Factory,
    private val editorDecomposeComponentFactory: EditorDecomposeComponent.Factory,
    private val storageProvider: FlipperStorageProvider,
    private val uploadFileDecomposeComponentFactory: UploadFileDecomposeComponent.Factory
) : FileManagerEditorDecomposeComponent<FileManagerEditorConfiguration>(),
    ComponentContext by componentContext {
    private val editorFileKeeper = instanceKeeper.getOrCreate {
        object : InstanceKeeper.Instance {
            val editorFile = storageProvider.getTemporaryFile()
            override fun onDestroy() {
                storageProvider.fileSystem.delete(editorFile)
            }
        }
    }

    override val stack: Value<ChildStack<FileManagerEditorConfiguration, DecomposeComponent>> =
        childStack(
            source = navigation,
            serializer = FileManagerEditorConfiguration.serializer(),
            initialStack = { listOf(FileManagerEditorConfiguration.Download(path)) },
            handleBackButton = true,
            childFactory = { config, childContext ->
                when (config) {
                    is FileManagerEditorConfiguration.Download -> {
                        fileDownloadDecomposeComponentFactory.invoke(
                            componentContext = childContext,
                            fullPathOnFlipper = path,
                            fullPathOnDevice = editorFileKeeper.editorFile,
                            onBack = { navigation.popOr(onBack::invoke) },
                            onDownloaded = {
                                navigation.replaceCurrent(
                                    FileManagerEditorConfiguration.Editor(
                                        fullPathOnFlipper = config.fullPathOnFlipper,
                                        tempPathOnDevice = editorFileKeeper.editorFile
                                    )
                                )
                            }
                        )
                    }

                    is FileManagerEditorConfiguration.Editor -> {
                        editorDecomposeComponentFactory.invoke(
                            componentContext = componentContext,
                            fullPathOnFlipper = config.fullPathOnFlipper,
                            fullPathOnDevice = config.tempPathOnDevice,
                            onBack = { navigation.popOr(onBack::invoke) },
                            editFinishedCallback = { fullPathOnFlipper ->
                                navigation.pushNew(
                                    FileManagerEditorConfiguration.Upload(
                                        fullPathOnFlipper = fullPathOnFlipper,
                                        tempPathOnDevice = editorFileKeeper.editorFile
                                    )
                                )
                            }
                        )
                    }

                    is FileManagerEditorConfiguration.Upload -> {
                        uploadFileDecomposeComponentFactory.invoke(
                            componentContext = childContext,
                            onBack = { navigation.popOr(onBack::invoke) },
                            fullPathOnFlipper = config.fullPathOnFlipper,
                            fullPathOnDevice = config.tempPathOnDevice,
                            onFinished = {
                                navigation.popOr(onBack::invoke)
                            },
                            onProgress = onFileChanged
                        )
                    }
                }
            },
        )
}
