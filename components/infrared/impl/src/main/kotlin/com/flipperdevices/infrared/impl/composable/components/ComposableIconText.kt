package com.flipperdevices.infrared.impl.composable.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.infrared.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableIconText(
    @StringRes textId: Int,
    @DrawableRes iconId: Int?,
    modifier: Modifier = Modifier,
) {
    ComposableIconText(
        text = stringResource(id = textId),
        painter = iconId?.let { painterResource(id = iconId) },
        modifier = modifier
    )
}

@Composable
internal fun ComposableIconText(
    text: String,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (painter != null) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(14.dp),
                painter = painter,
                contentDescription = text,
                tint = LocalPallet.current.warningColor
            )
        }
        Text(
            text = text,
            style = LocalTypography.current.subtitleM12,
            color = LocalPallet.current.text30
        )
    }
}

@Preview
@Composable
private fun ComposableIconTextPreview() {
    FlipperThemeInternal {
        ComposableIconText(
            iconId = DesignSystem.drawable.ic_warning,
            textId = R.string.infrared_connect_flipper
        )
    }
}
