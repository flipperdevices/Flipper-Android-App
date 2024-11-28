package com.flipperdevices.filemanager.editor.composable.download

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
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.ui.ktx.elements.FlipperProgressIndicator
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import okio.Path

@Composable
private fun InProgressTitleComposable(
    fullPathOnFlipper: Path,
    modifier: Modifier = Modifier
) {
    Text(
        text = fullPathOnFlipper.name,
        style = LocalTypography.current.titleB18,
        color = LocalPalletV2.current.text.title.primary,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun InProgressComposable(
    progress: Float,
    fullPathOnFlipper: Path,
    downloaded: Long,
    total: Long,
    speed: Long,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "Progress"
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InProgressTitleComposable(fullPathOnFlipper)
        Spacer(Modifier.height(12.dp))
        FlipperProgressIndicator(
            modifier = Modifier.padding(horizontal = 32.dp),
            accentColor = LocalPalletV2.current.action.blue.border.primary.default,
            secondColor = LocalPallet.current.actionOnFlipperProgress,
            painter = null,
            percent = animatedProgress
        )
        Spacer(Modifier.height(8.dp))
        if (total > 0) {
            Text(
                text = "${downloaded.toFormattedSize()}/${total.toFormattedSize()}",
                style = LocalTypography.current.subtitleM12,
                color = LocalPalletV2.current.text.body.secondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(8.dp))
        if (speed > 0) {
            Text(
                text = "Speed: ${speed.toFormattedSize()}",
                style = LocalTypography.current.subtitleM12,
                color = LocalPalletV2.current.text.body.secondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
