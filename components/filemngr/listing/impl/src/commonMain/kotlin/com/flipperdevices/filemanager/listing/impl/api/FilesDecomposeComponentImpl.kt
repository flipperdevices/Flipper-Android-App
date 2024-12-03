package com.flipperdevices.filemanager.listing.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.filemanager.create.api.CreateFileDecomposeComponent
import com.flipperdevices.filemanager.download.api.DownloadDecomposeComponent
import com.flipperdevices.filemanager.download.model.DownloadableFile
import com.flipperdevices.filemanager.listing.api.FilesDecomposeComponent
import com.flipperdevices.filemanager.listing.impl.composable.ComposableFileListScreen
import com.flipperdevices.filemanager.listing.impl.composable.LaunchedEventsComposable
import com.flipperdevices.filemanager.listing.impl.composable.modal.FileOptionsBottomSheet
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
import com.flipperdevices.filemanager.rename.api.RenameDecomposeComponent
import com.flipperdevices.filemanager.upload.api.UploadDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, FilesDecomposeComponent.Factory::class)
@Suppress("LongParameterList")
class FilesDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val pathChangedCallback: PathChangedCallback,
    @Assisted private val fileSelectedCallback: FileSelectedCallback,
    @Assisted private val searchCallback: SearchCallback,
    @Assisted private val moveToCallback: MoveToCallback,
    private val storageInfoViewModelFactory: Provider<StorageInfoViewModel>,
    private val optionsInfoViewModelFactory: Provider<OptionsViewModel>,
    private val deleteFilesViewModelFactory: Provider<DeleteFilesViewModel>,
    private val filesViewModelFactory: FilesViewModel.Factory,
    private val downloadDecomposeComponentFactory: DownloadDecomposeComponent.Factory,
    private val createSelectionViewModel: Provider<SelectionViewModel>,
    private val uploadDecomposeComponentFactory: UploadDecomposeComponent.Factory,
    private val renameDecomposeComponentFactory: RenameDecomposeComponent.Factory,
    private val createFileDecomposeComponentFactory: CreateFileDecomposeComponent.Factory,
) : FilesDecomposeComponent(componentContext) {

    private val slotNavigation = SlotNavigation<PathWithType>()

    val fileOptionsSlot: Value<ChildSlot<*, PathWithType>> = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        serializer = PathWithType.serializer(),
        childFactory = { bottomSheetFile, _ -> bottomSheetFile }
    )

    private val selectionViewModel = instanceKeeper.getOrCreate("selectionViewModel_$path") {
        createSelectionViewModel.get()
    }

    val filesViewModel = instanceKeeper.getOrCreate("filesViewModel_$path") {
        filesViewModelFactory.invoke(path)
    }

    private val uploadDecomposeComponent by lazy {
        uploadDecomposeComponentFactory.invoke(
            componentContext = childContext("FilesDecomposeComponent_uploadDecomposeComponent"),
            onFilesChanged = filesViewModel::onFilesChanged,
        )
    }
    private val downloadDecomposeComponent by lazy {
        downloadDecomposeComponentFactory.invoke(
            componentContext = childContext("FilesDecomposeComponent_downloadDecomposeComponent")
        )
    }

    private val renameDecomposeComponent by lazy {
        renameDecomposeComponentFactory.invoke(
            componentContext = childContext("FilesDecomposeComponent_renameDecomposeComponent"),
            renamedCallback = { oldFullPath, newFullPath ->
                filesViewModel.fileRenamed(oldFullPath, newFullPath)
            }
        )
    }

    private val createDecomposeComponent by lazy {
        createFileDecomposeComponentFactory.invoke(
            componentContext = childContext("FilesDecomposeComponent_createDecomposeComponent"),
            createCallback = { item ->
                filesViewModel.onFilesChanged(listOf(item))
            }
        )
    }

    private val backCallback = BackCallback {
        val parent = path.parent
        when {
            selectionViewModel.state.value.isEnabled -> {
                selectionViewModel.toggleMode()
            }

            downloadDecomposeComponent.isInProgress.value -> {
                downloadDecomposeComponent.onCancel()
            }

            parent == null -> {
                onBack.invoke()
            }

            else -> {
                pathChangedCallback.invoke(parent)
            }
        }
    }

    init {
        backHandler.register(backCallback)
    }

    override fun onFileChanged(listingItem: ListingItem) {
        filesViewModel.onFilesChanged(listOf(listingItem))
    }

    @Suppress("LongMethod")
    @Composable
    override fun Render() {
        val multipleFilesPicker = uploadDecomposeComponent.rememberMultipleFilesPicker(path)
        val storageInfoViewModel = viewModelWithFactory(path.root.toString()) {
            storageInfoViewModelFactory.get()
        }
        val optionsViewModel = viewModelWithFactory(path.root.toString()) {
            optionsInfoViewModelFactory.get()
        }
        val deleteFileViewModel = viewModelWithFactory(path.toString()) {
            deleteFilesViewModelFactory.get()
        }
        LaunchedEventsComposable(
            deleteFilesViewModel = deleteFileViewModel,
            onFileDelete = { path ->
                selectionViewModel.deselect(path)
                filesViewModel.fileDeleted(path)
            },
        )
        ComposableFileListScreen(
            path = path,
            deleteFileViewModel = deleteFileViewModel,
            filesViewModel = filesViewModel,
            optionsViewModel = optionsViewModel,
            storageInfoViewModel = storageInfoViewModel,
            selectionViewModel = selectionViewModel,
            onBack = onBack::invoke,
            onUploadClick = multipleFilesPicker::startFilePicker,
            onPathChange = pathChangedCallback::invoke,
            onFileMoreClick = slotNavigation::activate,
            onSearchClick = searchCallback::invoke,
            onEditFileClick = fileSelectedCallback::invoke,
            onRename = { pathWithType ->
                renameDecomposeComponent.startRename(pathWithType.fullPath, pathWithType.fileType)
            },
            canCreateFiles = createDecomposeComponent.canCreateFiles
                .collectAsState()
                .value,
            onCreate = { type ->
                createDecomposeComponent.startCreate(path, type)
            },
            onMove = { pathsWithType ->
                moveToCallback.invoke(pathsWithType.map(PathWithType::fullPath))
            },
            onExport = { pathsWithTypes ->
                pathsWithTypes.firstOrNull()?.let { pathWithType ->
                    DownloadableFile(
                        fullPath = pathWithType.fullPath,
                        size = pathWithType.size
                    )
                }?.run(downloadDecomposeComponent::download)
            }
        )
        FileOptionsBottomSheet(
            fileOptionsSlot = fileOptionsSlot,
            slotNavigation = slotNavigation,
            selectionViewModel = selectionViewModel,
            deleteFileViewModel = deleteFileViewModel,
            onDownloadFile = { pathWithType ->
                downloadDecomposeComponent.download(
                    file = DownloadableFile(
                        fullPath = pathWithType.fullPath,
                        size = pathWithType.size
                    )
                )
            },
            onRename = { pathWithType ->
                renameDecomposeComponent.startRename(pathWithType.fullPath, pathWithType.fileType)
            },
            onMoveTo = { pathWithType ->
                moveToCallback.invoke(listOf(pathWithType.fullPath))
            }
        )
        uploadDecomposeComponent.Render()
        downloadDecomposeComponent.Render()
        renameDecomposeComponent.Render()
        createDecomposeComponent.Render()
    }
}
