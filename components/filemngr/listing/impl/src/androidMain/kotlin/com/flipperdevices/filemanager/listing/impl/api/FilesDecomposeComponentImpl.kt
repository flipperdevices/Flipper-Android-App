package com.flipperdevices.filemanager.listing.impl.api

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.listing.api.FilesDecomposeComponent
import com.flipperdevices.filemanager.listing.impl.composable.ListingErrorComposable
import com.flipperdevices.filemanager.listing.impl.composable.NoFilesComposable
import com.flipperdevices.filemanager.listing.impl.composable.NoListingFeatureComposable
import com.flipperdevices.filemanager.listing.impl.composable.SdCardInfoComposable
import com.flipperdevices.filemanager.listing.impl.composable.options.ListOptionsDropDown
import com.flipperdevices.filemanager.listing.impl.viewmodel.CreateFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardComposable
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardPlaceholderComposable
import com.flipperdevices.filemanager.ui.components.itemcard.components.asPainter
import com.flipperdevices.filemanager.ui.components.itemcard.components.asTint
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemCardOrientation
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import com.flipperdevices.filemanager.ui.components.name.NameDialog
import com.flipperdevices.filemanager.ui.components.path.PathComposable
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path
import javax.inject.Provider
import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ContributesAssistedFactory(AppGraph::class, FilesDecomposeComponent.Factory::class)
class FilesDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onPathChanged: (Path) -> Unit,
    private val storageInfoViewModelFactory: Provider<StorageInfoViewModel>,
    private val optionsInfoViewModelFactory: Provider<OptionsViewModel>,
    private val createFileViewModelFactory: Provider<CreateFileViewModel>,
    private val filesViewModelFactory: FilesViewModel.Factory,
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
        val createFileState by createFileViewModel.state.collectAsState()
        val filesListState by filesViewModel.state.collectAsState()
        val optionsState by optionsViewModel.state.collectAsState()
        LaunchedEffect(createFileViewModel) {
            createFileViewModel.event.onEach {
                when (it) {
                    CreateFileViewModel.Event.FilesChanged -> {
                        filesViewModel.tryListFiles()
                    }
                }
            }.launchIn(this)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                OrangeAppBar(
                    title = "File Manager",
                    endBlock = {
                        Box {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 14.dp)
                                    .size(24.dp)
                                    .clickableRipple(onClick = optionsViewModel::toggleMenu),
                                painter = painterResource(DesignSystem.drawable.ic_more_points),
                                contentDescription = null,
                                tint = LocalPalletV2.current.icon.blackAndWhite.default
                            )
                            ListOptionsDropDown(
                                isVisible = optionsState.isVisible,
                                onDismiss = optionsViewModel::toggleMenu,
                                isHiddenFilesVisible = optionsState.isHiddenFilesVisible,
                                onSelectClick = {},
                                onCreateFileClick = {
                                    optionsViewModel.toggleMenu()
                                    createFileViewModel.onCreateFileClick()
                                },
                                onUploadClick = {},
                                onCreateFolderClick = {
                                    optionsViewModel.toggleMenu()
                                    createFileViewModel.onCreateFolderClick()
                                },
                                onGridClick = optionsViewModel::setGridOrientation,
                                onListClick = optionsViewModel::setListOrientation,
                                onSortBySizeClick = optionsViewModel::setSizeSort,
                                onSortByDefaultClick = optionsViewModel::setDefaultSort,
                                onShowHiddenFilesClick = optionsViewModel::toggleHiddenFiles,
                            )
                        }
                    },
                    onBack = onBack::invoke,
                )
            }
        ) { contentPadding ->
            when (val createFileState = createFileState) {
                CreateFileViewModel.State.Pending -> Unit
                is CreateFileViewModel.State.Visible -> {
                    NameDialog(
                        value = createFileState.name,
                        title = "Enter Name:",
                        buttonText = when (createFileState.currentAction) {
                            CreateFileViewModel.CreateFileAction.FILE -> "Create File"
                            CreateFileViewModel.CreateFileAction.FOLDER -> "Create Folder"
                        },
                        subtitle = "Allowed characters",
                        onFinish = { createFileViewModel.onCreate(path) },
                        isError = !createFileState.isValid,
                        isEnabled = !createFileState.isLoading,
                        needShowOptions = createFileState.needShowOptions,
                        onTextChange = createFileViewModel::onNameChange,
                        onDismissRequest = createFileViewModel::dismiss,
                        onOptionSelect = createFileViewModel::onOptionSelected,
                        options = createFileState.options
                    )
                }
            }
            val uiOrientation = remember(optionsState.orientation) {
                when (optionsState.orientation) {
                    is FileManagerOrientation.Unrecognized,
                    FileManagerOrientation.LIST -> ItemCardOrientation.LIST

                    FileManagerOrientation.GRID -> ItemCardOrientation.GRID
                }
            }
            when (val localFilesListState = filesListState) {
                FilesViewModel.State.CouldNotListPath -> {
                    ListingErrorComposable(
                        path = path,
                        onPathChange = onPathChanged,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is FilesViewModel.State.Loaded -> {
                    if (localFilesListState.files.isEmpty()) {
                        NoFilesComposable(
                            onUploadFilesClick = {},
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                else -> Unit
            }
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(14.dp),
                columns = when (uiOrientation) {
                    ItemCardOrientation.GRID -> GridCells.Fixed(2)
                    ItemCardOrientation.LIST -> GridCells.Fixed(1)
                }
            ) {
                if (path.root != path) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PathComposable(
                            path = path,
                            onRootPathClick = { path.root?.run(onPathChanged) },
                            onPathClick = onPathChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        )
                    }
                } else {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SdCardInfoComposable(storageInfoViewModel)
                    }
                }

                when (val localFilesListState = filesListState) {
                    is FilesViewModel.State.Loaded -> {
                        items(localFilesListState.files) { file ->
                            FolderCardComposable(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement(),
                                painter = file.asPainter(),
                                iconTint = file.asTint(),
                                title = file.fileName,
                                subtitle = file.size.toFormattedSize(),
                                selectionState = ItemUiSelectionState.NONE,
                                onClick = {
                                    if (file.fileType == FileType.DIR) {
                                        onPathChanged.invoke(path / file.fileName)
                                    }
                                },
                                onCheckChange = {},
                                onMoreClick = {},
                                onDelete = {},
                                orientation = ItemCardOrientation.LIST
                            )
                        }
                    }

                    FilesViewModel.State.Loading -> {
                        items(count = 6) {
                            Box(modifier = Modifier.animateItemPlacement()) {
                                FolderCardPlaceholderComposable(
                                    modifier = Modifier.fillMaxWidth(),
                                    orientation = ItemCardOrientation.LIST,
                                )
                            }
                        }
                    }

                    FilesViewModel.State.Unsupported -> {
                        item { NoListingFeatureComposable() }
                    }

                    FilesViewModel.State.CouldNotListPath -> Unit
                }
            }
        }
    }
}
