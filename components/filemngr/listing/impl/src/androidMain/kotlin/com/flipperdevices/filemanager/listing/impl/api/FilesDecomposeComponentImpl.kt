package com.flipperdevices.filemanager.listing.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.filemanager.listing.api.FilesDecomposeComponent
import com.flipperdevices.filemanager.listing.impl.composable.ComposableFileListScreen
import com.flipperdevices.filemanager.listing.impl.composable.LaunchedEventsComposable
import com.flipperdevices.filemanager.listing.impl.viewmodel.CreateFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
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
    @Assisted private val onPathChanged: (Path) -> Unit,
    @Assisted private val onUploadClick: () -> Unit,
    private val storageInfoViewModelFactory: Provider<StorageInfoViewModel>,
    private val optionsInfoViewModelFactory: Provider<OptionsViewModel>,
    private val createFileViewModelFactory: Provider<CreateFileViewModel>,
    private val deleteFilesViewModelFactory: Provider<DeleteFilesViewModel>,
    private val filesViewModelFactory: FilesViewModel.Factory,
    private val createSelectionViewModel: Provider<SelectionViewModel>
) : FilesDecomposeComponent(componentContext) {

    private val backCallback = BackCallback {
        val parent = path.parent
        if (parent == null) {
            onBack.invoke()
        } else {
            onPathChanged.invoke(parent)
        }
    }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    override fun Render() {
        val filesViewModel = viewModelWithFactory(path.toString()) {
            filesViewModelFactory.invoke(path)
        }
        val storageInfoViewModel = viewModelWithFactory(path.root.toString()) {
            storageInfoViewModelFactory.get()
        }
        val optionsViewModel = viewModelWithFactory(path.root.toString()) {
            optionsInfoViewModelFactory.get()
        }
        val createFileViewModel = viewModelWithFactory(path.root.toString()) {
            createFileViewModelFactory.get()
        }
        val deleteFileViewModel = viewModelWithFactory(path.toString()) {
            deleteFilesViewModelFactory.get()
        }
        val selectionViewModel = viewModelWithFactory(path.toString()) {
            createSelectionViewModel.get()
        }
        LaunchedEventsComposable(
            createFileViewModel = createFileViewModel,
            deleteFilesViewModel = deleteFileViewModel,
            onFileRemove = filesViewModel::fileDeleted,
            onFileListChange = filesViewModel::tryListFiles
        )
        ComposableFileListScreen(
            path = path,
            createFileViewModel = createFileViewModel,
            deleteFileViewModel = deleteFileViewModel,
            filesViewModel = filesViewModel,
            optionsViewModel = optionsViewModel,
            storageInfoViewModel = storageInfoViewModel,
            selectionViewModel = selectionViewModel,
            onBack = onBack::invoke,
            onUploadClick = onUploadClick,
            onPathChange = onPathChanged
        )
    }
}
