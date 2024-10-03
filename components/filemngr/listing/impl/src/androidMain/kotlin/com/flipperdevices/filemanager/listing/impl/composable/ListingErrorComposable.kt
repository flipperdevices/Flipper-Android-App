package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.filemanager.ui.components.path.PathComposable
import okio.Path

@Composable
fun ListingErrorComposable(
    path: Path,
    onPathChange: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        PathComposable(
            path = path,
            onRootPathClick = { path.root?.run(onPathChange) },
            onPathClick = onPathChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        )
        // todo
        Text("Could not list path")
    }
}
