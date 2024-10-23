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
import com.flipperdevices.ifrmvp.core.ui.button.core.buttonBackgroundVariantColor
import com.flipperdevices.ifrmvp.core.ui.button.core.onScrollHoldPress
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf
import com.flipperdevices.remotecontrols.core.ui.R as RemoteControlsR

@Composable
fun ShutterButtonComposable(
    onClick: (ButtonClickEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        ButtonPlaceholderBox(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(LocalPalletV2.current.surface.dialog.body.default)
                    .padding(12.sf)
                    .clip(CircleShape)
                    .background(buttonBackgroundVariantColor)
                    .onScrollHoldPress { onClick.invoke(it) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(RemoteControlsR.drawable.ic_rc_shutter),
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier.size(48.sf)
                )
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ShutterButtonComposablePreview() {
    FlipperThemeInternal {
        ShutterButtonComposable(
            onClick = {}
        )
    }
}
