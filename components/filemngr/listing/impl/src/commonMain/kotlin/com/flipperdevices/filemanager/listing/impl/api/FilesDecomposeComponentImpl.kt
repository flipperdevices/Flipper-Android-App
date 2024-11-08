package com.flipperdevices.filemanager.listing.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.filemanager.listing.api.FilesDecomposeComponent
import com.flipperdevices.filemanager.listing.impl.composable.ComposableFileListScreen
import com.flipperdevices.filemanager.listing.impl.composable.LaunchedEventsComposable
import com.flipperdevices.filemanager.listing.impl.composable.modal.FileOptionsBottomSheet
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.EditFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
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
    private val storageInfoViewModelFactory: Provider<StorageInfoViewModel>,
    private val optionsInfoViewModelFactory: Provider<OptionsViewModel>,
    private val editFileViewModelFactory: Provider<EditFileViewModel>,
    private val deleteFilesViewModelFactory: Provider<DeleteFilesViewModel>,
    private val filesViewModelFactory: FilesViewModel.Factory,
    private val createSelectionViewModel: Provider<SelectionViewModel>,
    private val uploadDecomposeComponentFactory: UploadDecomposeComponent.Factory,
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

    private val backCallback = BackCallback {
        val parent = path.parent
        when {
            selectionViewModel.state.value.isEnabled -> {
                selectionViewModel.toggleMode()
            }

            parent == null -> {
                onBack.invoke()
            }

            parent != null -> {
                pathChangedCallback.invoke(parent)
            }

            else -> {
                onBack.invoke()
            }
        }
    }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    override fun Render() {
        val multipleFilesPicker = uploadDecomposeComponent.rememberMultipleFilesPicker(path)
        val storageInfoViewModel = viewModelWithFactory(path.root.toString()) {
            storageInfoViewModelFactory.get()
        }
        val optionsViewModel = viewModelWithFactory(path.root.toString()) {
            optionsInfoViewModelFactory.get()
        }
        val createFileViewModel = viewModelWithFactory(path.root.toString()) {
            editFileViewModelFactory.get()
        }
        val deleteFileViewModel = viewModelWithFactory(path.toString()) {
            deleteFilesViewModelFactory.get()
        }
        LaunchedEventsComposable(
            editFileViewModel = createFileViewModel,
            deleteFilesViewModel = deleteFileViewModel,
            onFileRemove = filesViewModel::fileDeleted,
            onFileListChange = filesViewModel::tryListFiles
        )
        ComposableFileListScreen(
            path = path,
            editFileViewModel = createFileViewModel,
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
            onEditFileClick = fileSelectedCallback::invoke
        )
        FileOptionsBottomSheet(
            fileOptionsSlot = fileOptionsSlot,
            slotNavigation = slotNavigation,
            selectionViewModel = selectionViewModel,
            createFileViewModel = createFileViewModel,
            deleteFileViewModel = deleteFileViewModel
        )
        uploadDecomposeComponent.Render()
    }
}
