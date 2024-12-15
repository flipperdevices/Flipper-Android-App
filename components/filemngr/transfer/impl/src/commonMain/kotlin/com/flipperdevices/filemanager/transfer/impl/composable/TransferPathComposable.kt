package com.flipperdevices.filemanager.transfer.impl.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.filemanager.ui.components.path.PathComposable
import okio.Path

@Composable
fun TransferPathComposable(
    isMoving: Boolean,
    path: Path,
    onPathChange: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    PathComposable(
        path = path,
        onRootPathClick = onRootPathClick@{
            if (isMoving) return@onRootPathClick
            path.root?.run(onPathChange)
        },
        onPathClick = onPathClick@{ clickedPath ->
            if (isMoving) return@onPathClick
            onPathChange.invoke(clickedPath)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(14.dp)
    )
}
