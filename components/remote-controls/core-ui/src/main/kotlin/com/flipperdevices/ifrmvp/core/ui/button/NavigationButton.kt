package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.NoConnectionBox
import com.flipperdevices.ifrmvp.core.ui.button.core.SyncingBox
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf
import com.flipperdevices.ifrmvp.core.ui.layout.core.sfp
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants

private const val NAV_BUTTON_SCALE = 4

@Suppress("LongMethod")
@Composable
fun NavigationButton(
    onUpClick: () -> Unit,
    onRightClick: () -> Unit,
    onDownClick: () -> Unit,
    onLeftClick: () -> Unit,
    onOkClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = LocalPalletV2.current.surface.menu.body.dufault,
    iconTint: Color = MaterialTheme.colors.onPrimary,
    textColor: Color = MaterialTheme.colors.onPrimary,
) {
    Box(
        modifier = modifier
            .size(GridConstants.DEFAULT_BUTTON_SIZE.sf * NAV_BUTTON_SCALE)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = rememberVectorPainter(Icons.Filled.KeyboardArrowUp),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.sf)
                .clip(CircleShape)
                .clickable(onClick = onUpClick)
                .align(Alignment.TopCenter)
        )
        Icon(
            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowLeft),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.sf)
                .clip(CircleShape)
                .clickable(onClick = onLeftClick)
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .size(GridConstants.DEFAULT_BUTTON_SIZE.sf)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primaryVariant)
                .padding(4.sf)
                .clip(CircleShape)
                .background(LocalPalletV2.current.surface.menu.separator.default)
                .clip(CircleShape)
                .clickable(onClick = onOkClick)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center,
            content = {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.caption,
                    color = textColor,
                    fontSize = 16.sfp,
                    lineHeight = 2.sfp
                )
            }
        )

        Icon(
            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowRight),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.sf)
                .clip(CircleShape)
                .clickable(onClick = onRightClick)
                .align(Alignment.CenterEnd)
        )

        Icon(
            painter = rememberVectorPainter(Icons.Filled.KeyboardArrowDown),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.sf)
                .clip(CircleShape)
                .clickable(onClick = onDownClick)
                .align(Alignment.BottomCenter)
        )
        SyncingBox()
        NoConnectionBox()
    }
}
