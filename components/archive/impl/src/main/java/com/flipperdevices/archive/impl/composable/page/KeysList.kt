package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.composable.key.ComposableFlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKey

@SuppressWarnings("FunctionNaming")
fun LazyListScope.KeysList(
    keys: List<FlipperKey>?,
    onKeyClick: (FlipperKey) -> Unit
) {
    // If synchronization in progress yet
    if (keys == null) {
        item {
            Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 24.dp)
                )
            }
        }
        return
    }

    if (keys.isEmpty()) {
        item {
            Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.archive_content_empty))
            }
        }
    } else {
        items(keys.size) {
            ComposableFlipperKey(keys[it].path) {
                onKeyClick(keys[it])
            }
        }
    }
}
