package com.flipperdevices.filemanager.editor.composable.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // todo
        Text("Unsopported")
    }
}
