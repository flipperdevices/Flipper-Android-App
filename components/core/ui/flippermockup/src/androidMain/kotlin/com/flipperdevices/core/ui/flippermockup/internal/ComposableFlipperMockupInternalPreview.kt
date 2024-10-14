package com.flipperdevices.core.ui.flippermockup.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import flipperapp.components.core.ui.flippermockup.generated.resources.Res
import flipperapp.components.core.ui.flippermockup.generated.resources.template_white_flipper_active
import org.jetbrains.compose.resources.painterResource

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFlipperMockupInternalPreview() {
    FlipperThemeInternal {
        Column {
            ComposableFlipperMockupInternalRaw(
                modifier = Modifier.fillMaxWidth(),
                templatePic = painterResource(Res.drawable.template_white_flipper_active)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                )
            }
        }
    }
}
