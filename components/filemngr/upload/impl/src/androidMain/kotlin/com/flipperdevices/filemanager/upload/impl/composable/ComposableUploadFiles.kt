package com.flipperdevices.filemanager.upload.impl.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.filemanager.upload.impl.viewmodel.UploadViewModel

@Composable
fun ComposableUploadFiles(
    uploadViewModel: UploadViewModel,
    modifier: Modifier = Modifier
) {
    val state by uploadViewModel.state.collectAsState()
    val speedState by uploadViewModel.speedState.collectAsState(null)
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .systemBarsPadding()
    ) { contentPadding ->
        when (val localState = state) {
            is UploadViewModel.State.Uploading -> {
                UploadingComposable(
                    state = localState,
                    speed = speedState,
                    onCancel = { uploadViewModel.onCancel() },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            UploadViewModel.State.Cancelled,
            UploadViewModel.State.Error,
            UploadViewModel.State.Pending,
            UploadViewModel.State.Uploaded -> Unit
        }
    }
}
