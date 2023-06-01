package com.flipperdevices.faphub.installation.button.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installation.button.api.FapButtonSize

@Composable
fun ComposableFlipperButton(
    text: String,
    fapButtonSize: FapButtonSize,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    var buttonModifier = modifier
        .clip(RoundedCornerShape(6.dp))
        .background(color)
    if (onClick != null) {
        buttonModifier = buttonModifier
            .clickableRipple(onClick = onClick)
    }
    Box(
        modifier = buttonModifier
            .height(fapButtonSize.heightDp.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 21.dp),
            text = text,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.fapHubButtonText.copy(
                fontSize = fapButtonSize.textSizeSp.sp
            ),
            color = LocalPallet.current.onFapHubInstallButton
        )
    }
}

@Composable
fun ComposableDynamicFlipperButton(
    percent: Float,
    fapButtonSize: FapButtonSize,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(fapButtonSize.heightDp.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.5f))
            .border(3.dp, color, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        ComposableProgressRow(percent, color)
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 21.dp),
            text = percent.roundPercentToString(),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.fapHubButtonProgressText.copy(
                fontSize = fapButtonSize.textSizeSp.sp
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
