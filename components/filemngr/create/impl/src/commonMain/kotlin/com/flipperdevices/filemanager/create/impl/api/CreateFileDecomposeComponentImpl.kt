package com.flipperdevices.filemanager.create.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.create.api.CreateFileDecomposeComponent
import com.flipperdevices.filemanager.create.impl.viewmodel.CreateFileViewModel
import com.flipperdevices.filemanager.ui.components.name.NameDialog
import com.flipperdevices.filemanager.util.constant.FileManagerConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flipperapp.components.filemngr.create.impl.generated.resources.fmc_create_file_allowed_chars
import flipperapp.components.filemngr.create.impl.generated.resources.fmc_create_file_title
import flipperapp.components.filemngr.create.impl.generated.resources.fml_create_file_btn
import flipperapp.components.filemngr.create.impl.generated.resources.fml_create_folder_btn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path
import org.jetbrains.compose.resources.stringResource
import javax.inject.Provider
import flipperapp.components.filemngr.create.impl.generated.resources.Res as FMC

@ContributesAssistedFactory(AppGraph::class, CreateFileDecomposeComponent.Factory::class)
class CreateFileDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val createdCallback: CreatedCallback,
    renameViewModelProvider: Provider<CreateFileViewModel>
) : CreateFileDecomposeComponent(componentContext) {
    private val createFileViewModel = instanceKeeper.getOrCreate {
        renameViewModelProvider.get()
    }

    override val canCreateFiles = createFileViewModel.canCreateFiles

    override fun startCreateFile(parent: Path) {
        createFileViewModel.startCreateFile(parent)
    }

    override fun startCreateFolder(parent: Path) {
        createFileViewModel.startCreateFolder(parent)
    }

    override fun startCreate(parent: Path, type: FileType) {
        createFileViewModel.startCreate(parent, type)
    }

    @Composable
    override fun Render() {
        val state by createFileViewModel.state.collectAsState()
        LaunchedEffect(createFileViewModel) {
            createFileViewModel.event
                .onEach { event ->
                    when (event) {
                        is CreateFileViewModel.Event.Created -> {
                            createdCallback.invoke(event.item)
                        }
                    }
                }.launchIn(this)
        }
        when (val localState = state) {
            CreateFileViewModel.State.Pending -> Unit
            is CreateFileViewModel.State.Creating -> {
                NameDialog(
                    value = localState.name,
                    title = stringResource(FMC.string.fmc_create_file_title),
                    buttonText = when (localState.type) {
                        FileType.FILE -> stringResource(FMC.string.fml_create_file_btn)
                        FileType.DIR -> stringResource(FMC.string.fml_create_folder_btn)
                    },
                    subtitle = stringResource(
                        FMC.string.fmc_create_file_allowed_chars,
                        FileManagerConstants.FILE_NAME_AVAILABLE_CHARACTERS
                    ),
                    onFinish = createFileViewModel::onConfirm,
                    isError = !localState.isValid,
                    isEnabled = !localState.isCreating,
                    needShowOptions = localState.needShowOptions,
                    onTextChange = createFileViewModel::onNameChange,
                    onDismissRequest = createFileViewModel::dismiss,
                    onOptionSelect = createFileViewModel::onOptionSelected,
                    options = localState.options,
                    isLoading = localState.isCreating
                )
            }
        }
    }
}
