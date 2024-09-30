package com.flipperdevices.filemanager.listing.impl.api

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.filemanager.listing.api.FilesDecomposeComponent
import com.flipperdevices.filemanager.listing.impl.composable.ListingErrorComposable
import com.flipperdevices.filemanager.listing.impl.composable.NoFilesComposable
import com.flipperdevices.filemanager.listing.impl.composable.NoListingFeatureComposable
import com.flipperdevices.filemanager.listing.impl.composable.SdCardInfoComposable
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardComposable
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardPlaceholderComposable
import com.flipperdevices.filemanager.ui.components.itemcard.components.asPainter
import com.flipperdevices.filemanager.ui.components.itemcard.components.asTint
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemCardOrientation
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import com.flipperdevices.filemanager.ui.components.path.PathComposable
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, FilesDecomposeComponent.Factory::class)
class FilesDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onPathChanged: (Path) -> Unit,
    private val storageInfoViewModelFactory: Provider<StorageInfoViewModel>,
    private val filesViewModelFactory: FilesViewModel.Factory
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
        val filesListState by filesViewModel.state.collectAsState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                OrangeAppBar(
                    title = "File Manager",
                    endBlock = null,
                    onBack = onBack::invoke
                )
            }
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(14.dp)
            ) {
                if (path.root != path) {
                    item {
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
                    item {
                        SdCardInfoComposable(storageInfoViewModel)
                    }
                }

                when (val localFilesListState = filesListState) {
                    FilesViewModel.State.CouldNotListPath -> {
                        item {
                            ListingErrorComposable(
                                path = path,
                                onPathChange = onPathChanged,
                                modifier = Modifier.fillParentMaxSize()
                            )
                        }
                    }

                    is FilesViewModel.State.Loaded -> {
                        if (localFilesListState.files.isEmpty()) {
                            item {
                                NoFilesComposable(
                                    onUploadFilesClick = {},
                                    modifier = Modifier.fillParentMaxSize()
                                )
                            }
                        }

                        items(localFilesListState.files) { file ->
                            Box(modifier = Modifier.animateItemPlacement()) {
                                FolderCardComposable(
                                    modifier = Modifier.fillMaxWidth().animateItemPlacement(),
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
                }
            }
        }
    }
}
