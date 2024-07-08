package com.flipperdevices.ifrmvp.core.ui.layout.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun LoadingComposable(
    modifier: Modifier = Modifier,
    progress: Float = 0f
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = {
            Column {
                CircularProgressIndicator(
                    color = LocalPalletV2.current.action.brand.background.primary.default,
                    modifier = Modifier.size(54.dp)
                )
                if (progress != 0f) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = LocalTypography.current.bodySB14,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}
