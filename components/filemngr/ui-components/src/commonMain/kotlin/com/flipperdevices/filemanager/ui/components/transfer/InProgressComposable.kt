package com.flipperdevices.filemanager.ui.components.transfer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.FlipperProgressIndicator
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Suppress("LongMethod")
@Composable
internal fun FileTransferProgressComposable(
    progressTitle: String,
    progress: Float,
    modifier: Modifier = Modifier,
    progressText: String? = null,
    progressDetailText: String? = null,
    speedText: String? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "Progress"
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = progressTitle,
            style = LocalTypography.current.titleB18,
            color = LocalPalletV2.current.text.title.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        FlipperProgressIndicator(
            modifier = Modifier.padding(horizontal = 32.dp),
            accentColor = LocalPalletV2.current.action.blue.border.primary.default,
            secondColor = LocalPallet.current.actionOnFlipperProgress,
            painter = null,
            percent = animatedProgress
        )
        Spacer(Modifier.height(8.dp))
        AnimatedVisibility(progressDetailText != null) {
            if (progressDetailText != null) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = progressDetailText,
                        style = LocalTypography.current.subtitleM12,
                        color = LocalPalletV2.current.text.body.secondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        AnimatedVisibility(progressText != null) {
            if (progressText != null) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = progressText,
                        style = LocalTypography.current.subtitleM12,
                        color = LocalPalletV2.current.text.body.secondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        AnimatedVisibility(speedText != null) {
            if (speedText != null) {
                Text(
                    text = speedText,
                    style = LocalTypography.current.subtitleM12,
                    color = LocalPalletV2.current.text.body.secondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
