package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okio.Path

@Composable
fun LaunchedEventsComposable(
    deleteFilesViewModel: DeleteFilesViewModel,
    onFileDelete: (Path) -> Unit
) {
    LaunchedEffect(deleteFilesViewModel) {
        deleteFilesViewModel.event.onEach {
            when (it) {
                DeleteFilesViewModel.Event.CouldNotDeleteSomeFiles -> Unit
                is DeleteFilesViewModel.Event.FileDeleted -> onFileDelete.invoke(it.path)
            }
        }.launchIn(this)
    }
}
