package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.emulate.composable.common.button.ComposableEmulateButton
import com.flipperdevices.keyscreen.emulate.model.EmulateProgress
import com.flipperdevices.keyscreen.emulate.model.Picture

@Composable
fun ComposableEmulateButtonWithText(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    progress: EmulateProgress? = null,
    @StringRes buttonTextId: Int,
    @StringRes textId: Int? = null,
    @DrawableRes iconId: Int? = null,
    picture: Picture? = null,
    color: Color,
    progressColor: Color = Color.Transparent
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComposableEmulateButton(
            buttonContentModifier = buttonModifier,
            emulateProgress = progress,
            textId = buttonTextId,
            picture = picture,
            color = color,
            progressColor = progressColor
        )

        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconId != null) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(14.dp),
                    painter = painterResource(iconId),
                    contentDescription = textId?.let { stringResource(it) } ?: "",
                    tint = LocalPallet.current.warningColor
                )
            }
            Text(
                text = textId?.let { stringResource(it) } ?: "",
                style = LocalTypography.current.subtitleM12,
                color = LocalPallet.current.text30
            )
        }
    }
}
