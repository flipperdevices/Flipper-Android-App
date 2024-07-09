package com.flipperdevices.ifrmvp.core.ui.layout.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import kotlin.math.roundToInt

private const val MAX_PROGRESS = 100

@Composable
fun LoadingComposable(
    modifier: Modifier = Modifier,
    progress: Float = 0f
) {
    val progressFormatter = remember(progress) {
        "${(progress * MAX_PROGRESS).roundToInt()}%"
    }
    val isProgressShown = remember(progress) {
        progress != 0f
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = LocalPalletV2.current.action.brand.background.primary.default,
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    text = progressFormatter.takeIf { isProgressShown }.orEmpty(),
                    style = LocalTypography.current.bodySB14,
                    color = MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}
