package com.flipperdevices.filemanager.transfer.impl.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.filemanager.listing.api.model.ExtendedListingItem
import com.flipperdevices.filemanager.transfer.api.TransferDecomposeComponent
import com.flipperdevices.filemanager.transfer.api.model.TransferType
import com.flipperdevices.filemanager.transfer.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.transfer.impl.viewmodel.TransferViewModel
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardListComposable
import com.flipperdevices.filemanager.ui.components.itemcard.components.asPainter
import com.flipperdevices.filemanager.ui.components.itemcard.components.asTint
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import com.flipperdevices.filemanager.ui.components.path.PathComposable
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, TransferDecomposeComponent.Factory::class)
class TransferDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: Param,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onMoved: MovedCallback,
    @Assisted private val onPathChange: PathChangedCallback,
    filesViewModelFactory: FilesViewModel.Factory,
    transferViewModelProvider: Provider<TransferViewModel>
) : TransferDecomposeComponent(componentContext) {
    private val filesViewModel = instanceKeeper.getOrCreate("files_${param.path}") {
        filesViewModelFactory.invoke(path = param.path)
    }
    private val transferViewModel = instanceKeeper.getOrCreate("transfer_${param.path}") {
        transferViewModelProvider.get()
    }

    private val backCallback = BackCallback {
        val parent = param.path.parent
        if (transferViewModel.state.value is TransferViewModel.State.Moving) return@BackCallback
        if (parent == null) {
            onBack.invoke()
        } else {
            onPathChange.invoke(parent)
        }
    }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    override fun Render() {
        LaunchedEffect(transferViewModel) {
            transferViewModel.state
                .filterIsInstance<TransferViewModel.State.Moved>()
                .onEach { state ->
                    onMoved.invoke(state.targetDir)
                }
                .launchIn(this)
        }

        val state by filesViewModel.state.collectAsState()
        val transferState by transferViewModel.state.collectAsState()
        val isMoving = transferState is TransferViewModel.State.Moving
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                OrangeAppBar(
                    title = when (param.transferType) {
                        TransferType.MOVE -> "Moving"
                    },
                    onBack = onBack::invoke
                )
            }
        ) { contentPaddings ->
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(14.dp),
                columns = GridCells.Fixed(1)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    PathComposable(
                        path = param.path,
                        onRootPathClick = onRootPathClick@{
                            if (isMoving) return@onRootPathClick
                            param.path.root?.run(onPathChange::invoke)
                        },
                        onPathClick = onPathClick@{ path ->
                            if (isMoving) return@onPathClick
                            onPathChange.invoke(path)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    )
                }

                when (val localState = state) {
                    FilesViewModel.State.CouldNotListPath -> {
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                            content = { Box(Modifier.fillMaxSize().background(Color.Red)) }
                        )
                    }

                    is FilesViewModel.State.Loaded -> {
                        items(localState.files) { file ->
                            FolderCardListComposable(
                                painter = file.asListingItem().asPainter(),
                                iconTint = file.asListingItem().asTint(),
                                title = file.itemName,
                                subtitle = when (file) {
                                    is ExtendedListingItem.File -> file.size.toFormattedSize()
                                    is ExtendedListingItem.Folder -> "items ${file.itemsCount ?: 0}"
                                },
                                isSubtitleLoading = when (file) {
                                    is ExtendedListingItem.File -> false
                                    is ExtendedListingItem.Folder -> file.itemsCount == null
                                },
                                selectionState = ItemUiSelectionState.NONE,
                                onClick = onClick@{
                                    if (file.itemType != FileType.DIR) return@onClick
                                    if (isMoving) return@onClick
                                    onPathChange.invoke(param.path.resolve(file.itemName))
                                },
                                onCheckChange = null,
                                onMoreClick = null
                            )
                        }
                        item {
                            Box(Modifier.height(32.dp))
                        }
                    }

                    FilesViewModel.State.Loading -> {
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                            content = { Box(Modifier.fillMaxSize().background(Color.Blue)) }
                        )
                    }

                    FilesViewModel.State.Unsupported -> {
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                            content = { Box(Modifier.fillMaxSize().background(Color.Magenta)) }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
                content = {
                    ComposableFlipperButton(
                        text = "Move Here",
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = isMoving,
                        enabled = !isMoving &&
                            !param.fullPathToMove.contains(param.path) &&
                            !param.fullPathToMove.mapNotNull(Path::parent).contains(param.path),
                        onClick = {
                            transferViewModel.move(
                                oldPaths = param.fullPathToMove,
                                targetDir = param.path
                            )
                        }
                    )
                }
            )
        }
    }
}
