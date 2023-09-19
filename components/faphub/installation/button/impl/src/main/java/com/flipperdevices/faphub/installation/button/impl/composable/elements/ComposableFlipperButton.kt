package com.flipperdevices.faphub.installation.button.impl.composable.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.R

@Composable
fun ComposableFlipperButton(
    text: String,
    fapButtonSize: FapButtonSize,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    var buttonModifier = modifier
        .border(2.dp, color, RoundedCornerShape(6.dp))
    if (onClick != null) {
        buttonModifier = buttonModifier
            .clickableRipple(onClick = onClick)
    }
    buttonModifier = when (fapButtonSize) {
        FapButtonSize.COMPACTED -> buttonModifier.width(92.dp)
        FapButtonSize.LARGE -> buttonModifier
    }
    Box(
        modifier = buttonModifier
            .height(fapButtonSize.heightDp.dp),
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
            color = color
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFlipperButtonPreview() {
    FlipperThemeInternal {
        Box {
            ComposableFlipperButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.faphub_installation_install),
                color = LocalPallet.current.accent,
                fapButtonSize = FapButtonSize.LARGE,
                onClick = { }
            )
        }
    }
}