package com.flipperdevices.filemanager.ui.components.transfer

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

@Composable
fun FileTransferFullScreenComposable(
    title: String,
    actionText: String,
    onActionClick: () -> Unit,
    progressTitle: String,
    progress: Float,
    modifier: Modifier = Modifier,
    progressText: String? = null,
    progressDetailText: String? = null,
    speedText: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
                text = actionText,
                style = LocalTypography.current.bodyM14,
                color = LocalPalletV2.current.action.danger.text.default,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickableRipple(onClick = onActionClick)
                    .padding(12.dp),
                textAlign = TextAlign.Center
            )
        }
        FileTransferProgressComposable(
            progressTitle = progressTitle,
            progressDetailText = progressDetailText,
            progress = progress,
            progressText = progressText,
            speedText = speedText
        )
    }
}
