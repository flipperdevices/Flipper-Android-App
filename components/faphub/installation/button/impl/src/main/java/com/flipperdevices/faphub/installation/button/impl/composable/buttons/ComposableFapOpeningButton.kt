package com.flipperdevices.faphub.installation.button.impl.composable.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.sweep.animatedRotatableBrush
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.R

private const val PROGRESS_BAR_SIZE = 4

@Composable
internal fun ComposableFapOpeningButton(
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier,
) {
    val text = when (fapButtonSize) {
        FapButtonSize.COMPACTED -> stringResource(R.string.faphub_installation_open)
        FapButtonSize.LARGE -> stringResource(R.string.faphub_installation_open_long)
    }

    val progressColor = LocalPallet.current.fapHubOpenAppProgress
    val buttonColor = LocalPallet.current.fapHubOpenAppEnable

    var buttonModifier = modifier.clip(RoundedCornerShape(6.dp))
    buttonModifier = when (fapButtonSize) {
        FapButtonSize.COMPACTED -> buttonModifier.width(92.dp)
        FapButtonSize.LARGE -> buttonModifier
    }

    val buttonClip = RoundedCornerShape(6.dp)
    val buttonProgressBackground = animatedRotatableBrush(
        backgroundColor = progressColor.copy(alpha = 0.4f),
        cursorColor = progressColor,
        tag = "ComposableFapOpeningButton"
    )

    Box(
        modifier = buttonModifier
            .height(fapButtonSize.heightDp.dp)
            .clip(buttonClip)
            .background(buttonProgressBackground)
            .padding(PROGRESS_BAR_SIZE.dp)
            .clip(buttonClip)
            .background(buttonColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = text,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.fapHubButtonText.copy(
                fontSize = fapButtonSize.textSizeSp.sp
            ),
            maxLines = 1,
            color = LocalPallet.current.onFapHubInstallButton
        )
    }
}

@Preview
@Composable
private fun PreviewComposableFapOpeningButton() {
    FlipperThemeInternal {
        Column {
            ComposableFapOpeningButton(FapButtonSize.COMPACTED)
            ComposableFapOpeningButton(FapButtonSize.LARGE)
        }
    }
}
