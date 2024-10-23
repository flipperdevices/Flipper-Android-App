package com.flipperdevices.filemanager.ui.components.path

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import okio.Path.Companion.toPath

@Preview
@Composable
private fun PathComposablePreview() {
    var path by remember { mutableStateOf("/folder/folder2/folder3/folder4/folder6/folder7/folder8".toPath()) }
    FlipperThemeInternal {
        Column(modifier = Modifier.width(500.dp)) {
            PathComposable(
                path = path,
                onPathClick = {
                    path = it
                },
                onRootPathClick = {
                    path = "/".toPath()
                }
            )
        }
    }
}
