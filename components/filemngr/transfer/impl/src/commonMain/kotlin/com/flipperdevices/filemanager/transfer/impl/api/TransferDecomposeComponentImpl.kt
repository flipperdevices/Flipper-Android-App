package com.flipperdevices.filemanager.transfer.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.create.api.CreateFileDecomposeComponent
import com.flipperdevices.filemanager.transfer.api.TransferDecomposeComponent
import com.flipperdevices.filemanager.transfer.impl.composable.ComposableTransferScreen
import com.flipperdevices.filemanager.transfer.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.transfer.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.transfer.impl.viewmodel.TransferViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, TransferDecomposeComponent.Factory::class)
class TransferDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: Param,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onMoved: MovedCallback,
    @Assisted private val onPathChange: PathChangedCallback,
    filesViewModelFactory: FilesViewModel.Factory,
    transferViewModelProvider: Provider<TransferViewModel>,
    optionsViewModelProvider: Provider<OptionsViewModel>,
    createFileDecomposeComponentFactory: CreateFileDecomposeComponent.Factory,
) : TransferDecomposeComponent(componentContext) {
    private val filesViewModel = instanceKeeper.getOrCreate("files_${param.path}") {
        filesViewModelFactory.invoke(path = param.path)
    }
    private val transferViewModel = instanceKeeper.getOrCreate("transfer_${param.path}") {
        transferViewModelProvider.get()
    }
    private val optionsViewModel = instanceKeeper.getOrCreate("options_${param.path}") {
        optionsViewModelProvider.get()
    }
    private val createFileDecomposeComponent = createFileDecomposeComponentFactory.invoke(
        componentContext = childContext("createfolder_${param.path}"),
        createCallback = filesViewModel::onFolderCreated
    )

    // Folder should not be a subfolder of movable folder
    // Folder should be the same as movable folder's parent
    private val canMoveHere = param.fullPathToMove
        .all { path -> !param.path.toString().startsWith(path.toString()) }
        .and(param.fullPathToMove.all { path -> param.path != path.parent })

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
                .onEach { state -> onMoved.invoke(state.targetDir) }
                .launchIn(this)
        }

        val optionsState by optionsViewModel.state.collectAsState()
        val state by filesViewModel.state.collectAsState()
        val transferState by transferViewModel.state.collectAsState()
        val isMoving = transferState is TransferViewModel.State.Moving
        ComposableTransferScreen(
            optionsState = optionsState,
            state = state,
            isMoving = isMoving,
            canMoveHere = canMoveHere,
            param = param,
            onBack = onBack::invoke,
            onOptionsAction = optionsViewModel::onAction,
            onCreateFolder = { createFileDecomposeComponent.startCreateFolder(param.path) },
            onPathChange = onPathChange::invoke,
            onMoveStart = {
                transferViewModel.move(
                    oldPaths = param.fullPathToMove,
                    targetDir = param.path
                )
            },
        )
        createFileDecomposeComponent.Render()
    }
}
