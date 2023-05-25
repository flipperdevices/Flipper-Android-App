package com.flipperdevices.faphub.installation.button.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableFlipperButton(
    text: String,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 21.dp),
            text = text,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.fapHubButtonText.copy(
                fontSize = fontSize
            ),
            color = LocalPallet.current.onFapHubInstallButton
        )
    }
}

@Composable
fun ComposableDynamicFlipperButton(
    percent: Float,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.5f))
            .border(3.dp, color, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        ComposableProgressRow(percent, color)
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 21.dp),
            text = percent.roundPercentToString(),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.fapHubButtonProgressText.copy(
                fontSize = fontSize
            ),
            color = LocalPallet.current.onFapHubInstallButton
        )
    }
}

@Composable
private fun BoxScope.ComposableProgressRow(percent: Float, accentColor: Color) {
    Row(
        modifier = Modifier
            .matchParentSize()
    ) {
        val remainingWeight = 1.0f - percent
        if (percent > 0.0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(percent)
                    .background(accentColor)
            )
        }

        if (remainingWeight > 0.0f) {
            Box(modifier = Modifier.weight(remainingWeight))
        }
    }
}
