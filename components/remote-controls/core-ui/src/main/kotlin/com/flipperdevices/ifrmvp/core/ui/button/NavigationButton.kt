package com.flipperdevices.ifrmvp.core.ui.button

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.ButtonClickEvent
import com.flipperdevices.ifrmvp.core.ui.button.core.ButtonPlaceholderBox
import com.flipperdevices.ifrmvp.core.ui.button.core.buttonBackgroundColor
import com.flipperdevices.ifrmvp.core.ui.button.core.buttonBackgroundVariantColor
import com.flipperdevices.ifrmvp.core.ui.button.core.onScrollHoldPress
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants
import com.flipperdevices.remotecontrols.core.ui.R as RemoteControlsR

@Suppress("LongMethod")
@Composable
fun NavigationButton(
    onUpClick: (ButtonClickEvent) -> Unit,
    onRightClick: (ButtonClickEvent) -> Unit,
    onDownClick: (ButtonClickEvent) -> Unit,
    onLeftClick: (ButtonClickEvent) -> Unit,
    onOkClick: ((ButtonClickEvent) -> Unit)?,
    modifier: Modifier = Modifier,
    background: Color = buttonBackgroundColor,
    iconTint: Color = Color.White,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        ButtonPlaceholderBox(modifier = Modifier.fillMaxSize()) {
            Icon(
                painter = painterResource(RemoteControlsR.drawable.ic_rc_up),
                tint = iconTint,
                contentDescription = null,
                modifier = Modifier
                    .size(32.sf)
                    .clip(CircleShape)
                    .onScrollHoldPress { onUpClick.invoke(it) }
                    .align(Alignment.TopCenter)
            )
            Icon(
                painter = painterResource(RemoteControlsR.drawable.ic_rc_left),
                tint = iconTint,
                contentDescription = null,
                modifier = Modifier
                    .size(32.sf)
                    .clip(CircleShape)
                    .onScrollHoldPress { onLeftClick.invoke(it) }
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
                        .background(buttonBackgroundVariantColor)
                        .clip(CircleShape)
                        .onScrollHoldPress { clickEvent -> onOkClick.invoke(clickEvent) }
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            painter = painterResource(RemoteControlsR.drawable.ic_rc_ok),
                            tint = iconTint,
                            contentDescription = null,
                            modifier = Modifier.size(72.sf)
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
                    .onScrollHoldPress { onRightClick.invoke(it) }
                    .align(Alignment.CenterEnd)
            )

            Icon(
                painter = painterResource(RemoteControlsR.drawable.ic_rc_down),
                tint = iconTint,
                contentDescription = null,
                modifier = Modifier
                    .size(32.sf)
                    .clip(CircleShape)
                    .onScrollHoldPress { onDownClick.invoke(it) }
                    .align(Alignment.BottomCenter)
            )
        }
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
