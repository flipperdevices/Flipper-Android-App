package com.flipperdevices.faphub.installation.button.impl.composable.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.R

@Composable
fun ComposableInProgressFapButton(
    percent: Float,
    fapButtonSize: FapButtonSize,
    color: Color,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    fapButtonModifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (fapButtonSize) {
            FapButtonSize.COMPACTED -> {}
            FapButtonSize.LARGE -> ComposableInProgressFapCancel(
                Modifier
                    .padding(end = 12.dp)
                    .size(46.dp)
                    .clickableRipple(onClick = onCancel)
            )
        }
        ComposableInProgressFapButtonInternal(
            percent = percent,
            fapButtonSize = fapButtonSize,
            color = color,
            modifier = fapButtonModifier
        )
        when (fapButtonSize) {
            FapButtonSize.COMPACTED -> ComposableInProgressFapCancel(
                Modifier
                    .padding(start = 12.dp)
                    .size(34.dp)
                    .clickableRipple(onClick = onCancel)
            )

            FapButtonSize.LARGE -> {}
        }
    }
}

@Composable
private fun ComposableInProgressFapCancel(
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_cancel),
        contentDescription = stringResource(R.string.faphub_installation_cancel_desc),
        tint = LocalPallet.current.text40
    )
}

@Composable
private fun ComposableInProgressFapButtonInternal(
    percent: Float,
    fapButtonSize: FapButtonSize,
    color: Color,
    modifier: Modifier = Modifier
) {
    val buttonModifier = when (fapButtonSize) {
        FapButtonSize.COMPACTED -> modifier.width(92.dp)
        FapButtonSize.LARGE -> modifier
    }
    Box(
        modifier = buttonModifier
            .height(fapButtonSize.heightDp.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.5f))
            .border(3.dp, color, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        ComposableProgressRow(percent, color)
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = percent.roundPercentToString(),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.fapHubButtonProgressText.copy(
                fontSize = fapButtonSize.textSizeSp.sp
            ),
            maxLines = 1,
            color = LocalPallet.current.onFapHubInstallButton
        )
    }
}

@Composable
private fun BoxScope.ComposableProgressRow(percent: Float, accentColor: Color) {
    val animatedPercent by animateFloatAsState(targetValue = percent)
    Row(
        modifier = Modifier
            .matchParentSize()
    ) {
        val remainingWeight = 1.0f - animatedPercent
        if (animatedPercent > 0.0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(animatedPercent)
                    .background(accentColor)
            )
        }

        if (remainingWeight > 0.0f) {
            Box(modifier = Modifier.weight(remainingWeight))
        }
    }
}
