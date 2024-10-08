package com.flipperdevices.filemanager.ui.components.itemcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2

@Composable
fun FolderCardPlaceholderComposable(
    orientation: FileManagerOrientation,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LocalPalletV2.current.surface.contentCard.body.default)
            .padding(12.dp),
        contentAlignment = Alignment.TopStart
    ) {
        @Composable
        fun OrientationContent() {
            Box(
                Modifier
                    .size(32.dp)
                    .placeholderConnecting()
            )
            Box(
                Modifier
                    .width(84.dp)
                    .height(16.dp)
                    .placeholderConnecting()
            )
        }
        when (orientation) {
            FileManagerOrientation.GRID -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OrientationContent()
                }
            }

            is FileManagerOrientation.Unrecognized,
            FileManagerOrientation.LIST -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OrientationContent()
                }
            }
        }
    }
}

@Preview
@Composable
private fun FolderCardPlaceholderComposablePreview() {
    FlipperThemeInternal {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(
                FileManagerOrientation.LIST,
                FileManagerOrientation.GRID
            ).forEach { orientation ->
                FolderCardPlaceholderComposable(orientation = orientation)
            }
        }
    }
}
