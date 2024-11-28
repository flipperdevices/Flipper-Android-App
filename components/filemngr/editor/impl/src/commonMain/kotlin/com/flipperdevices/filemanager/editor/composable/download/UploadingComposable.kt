package com.flipperdevices.filemanager.editor.composable.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import okio.Path

@Composable
fun UploadingComposable(
    title: String,
    progress: Float,
    fullPathOnFlipper: Path,
    current: Long,
    max: Long,
    speed: Long,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                style = LocalTypography.current.titleB18,
                color = LocalPalletV2.current.text.title.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Cancel",
                style = LocalTypography.current.bodyM14,
                color = LocalPalletV2.current.action.danger.text.default,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickableRipple(onClick = onCancel),
                textAlign = TextAlign.Center
            )
        }
        InProgressComposable(
            progress = progress,
            speed = speed,
            fullPathOnFlipper = fullPathOnFlipper,
            downloaded = current,
            total = max
        )
    }
}
