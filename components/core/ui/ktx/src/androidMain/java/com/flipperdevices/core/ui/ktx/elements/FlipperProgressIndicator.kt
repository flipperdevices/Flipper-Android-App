package com.flipperdevices.core.ui.ktx.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import kotlin.math.roundToInt

private const val PERCENT_MAX = 100
private const val PERCENT_MIN = 0.0001f

@Composable
fun FlipperProgressIndicator(
    accentColor: Color,
    secondColor: Color,
    @DrawableRes iconId: Int?,
    percent: Float?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 46.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(secondColor)
            .border(3.dp, accentColor, RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        if (percent != null) {
            ComposableProgressRow(percent, accentColor)
        }

        if (iconId != null) {
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .size(28.dp),
                painter = painterResourceByKey(iconId),
                contentDescription = null,
                tint = LocalPallet.current.onFirmwareUpdateProgress
            )
        }

        val progressText = if (percent != null) {
            "${(percent * PERCENT_MAX).roundToInt()}%"
        } else {
            animatedDots()
        }

        Text(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 6.dp)
                .fillMaxWidth(),
            text = progressText,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.updateText40,
            color = LocalPallet.current.onFirmwareUpdateProgress
        )
    }
}

@Composable
private fun BoxScope.ComposableProgressRow(percent: Float, accentColor: Color) {
    Row(
        modifier = Modifier
            .matchParentSize()
    ) {
        val wrapPercent = if (percent <= 0f) {
            PERCENT_MIN
        } else {
            percent
        }

        val remainingWeight = 1.0f - wrapPercent

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(wrapPercent)
                .background(accentColor)
        )

        if (remainingWeight > 0.0f) {
            Box(
                modifier = Modifier
                    .weight(remainingWeight)
            )
        }
    }
}
