package com.flipperdevices.filemanager.editor.composable.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent

@Composable
fun UploaderDecomposeComponent.RenderLoadingScreen(modifier: Modifier = Modifier) {
    val uploaderState by state.collectAsState()
    AnimatedVisibility(
        visible = uploaderState is UploaderDecomposeComponent.State.Uploading,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        val speedState by speedState.collectAsState(null)
        Render(
            state = uploaderState,
            speedState = speedState,
            onCancelClick = ::onCancel,
            modifier = Modifier
                .fillMaxSize()
                .background(LocalPalletV2.current.surface.backgroundMain.body)
                .navigationBarsPadding()
                .systemBarsPadding(),
        )
    }
}
