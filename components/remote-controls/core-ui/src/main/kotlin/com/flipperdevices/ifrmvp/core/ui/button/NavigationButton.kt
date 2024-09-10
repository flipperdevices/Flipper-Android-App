package com.flipperdevices.ifrmvp.core.ui.button

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.EmulatingBox
import com.flipperdevices.ifrmvp.core.ui.button.core.NoConnectionBox
import com.flipperdevices.ifrmvp.core.ui.button.core.SyncingBox
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants
import com.flipperdevices.remotecontrols.core.ui.R as RemoteControlsR

@Suppress("LongMethod")
@Composable
fun NavigationButton(
    onUpClick: () -> Unit,
    onRightClick: () -> Unit,
    onDownClick: () -> Unit,
    onLeftClick: () -> Unit,
    onOkClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    background: Color = LocalPalletV2.current.surface.menu.body.dufault,
    iconTint: Color = MaterialTheme.colors.onPrimary,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(RemoteControlsR.drawable.ic_rc_up),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.sf)
                .clip(CircleShape)
                .clickable(onClick = onUpClick)
                .align(Alignment.TopCenter)
        )
        Icon(
            painter = painterResource(RemoteControlsR.drawable.ic_rc_left),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.sf)
                .clip(CircleShape)
                .clickable(onClick = onLeftClick)
                .align(Alignment.CenterStart)
        )
        onOkClick?.let {
            Box(
                modifier = Modifier
                    .size(GridConstants.DEFAULT_BUTTON_SIZE.sf)
                    .clip(CircleShape)
                    .background(LocalPalletV2.current.surface.sheet.body.default)
                    .padding(4.sf)
                    .clip(CircleShape)
                    .background(LocalPalletV2.current.surface.menu.separator.default)
                    .clip(CircleShape)
                    .clickable(onClick = onOkClick)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center,
                content = {
                    Icon(
                        painter = painterResource(RemoteControlsR.drawable.ic_rc_ok),
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = null,
                        modifier = Modifier.size(48.sf)
                    )
                }
            )
        }

        Icon(
            painter = painterResource(RemoteControlsR.drawable.ic_rc_right),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.sf)
                .clip(CircleShape)
                .clickable(onClick = onRightClick)
                .align(Alignment.CenterEnd)
        )

        Icon(
            painter = painterResource(RemoteControlsR.drawable.ic_rc_down),
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
        EmulatingBox()
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NavigationButtonPreview() {
    FlipperThemeInternal {
        NavigationButton(
            onOkClick = {},
            onUpClick = {},
            onRightClick = {},
            onDownClick = {},
            onLeftClick = {},
        )
    }
}
