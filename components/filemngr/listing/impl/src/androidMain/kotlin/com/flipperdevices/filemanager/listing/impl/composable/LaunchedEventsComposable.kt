package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.flipperdevices.filemanager.listing.impl.viewmodel.CreateFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okio.Path

@Composable
fun LaunchedEventsComposable(
    createFileViewModel: CreateFileViewModel,
    deleteFilesViewModel: DeleteFilesViewModel,
    onFileListChange: () -> Unit,
    onFileRemove: (Path) -> Unit
) {
    LaunchedEffect(createFileViewModel, deleteFilesViewModel) {
        createFileViewModel.event.onEach {
            when (it) {
                CreateFileViewModel.Event.FilesChanged -> {
                    onFileListChange.invoke()
                }
            }
        }.launchIn(this)
        deleteFilesViewModel.event.onEach {
            when (it) {
                DeleteFilesViewModel.Event.CouldNotDeleteSomeFiles -> Unit
                is DeleteFilesViewModel.Event.FileDeleted -> onFileRemove.invoke(it.path)
            }
        }.launchIn(this)
    }
}
