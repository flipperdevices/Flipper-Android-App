package com.flipperdevices.core.ui.flippermockup.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import flipperapp.components.core.ui.flippermockup.generated.resources.Res
import flipperapp.components.core.ui.flippermockup.generated.resources.pic_flipperscreen_default
import flipperapp.components.core.ui.flippermockup.generated.resources.template_white_flipper_active
import org.jetbrains.compose.resources.painterResource

@Preview
@Composable
private fun PreviewComposableFlipperMockup() {
    FlipperThemeInternal {
        Column {
            ComposableFlipperMockupInternal(
                modifier = Modifier.fillMaxWidth(),
                templatePicPainter = painterResource(Res.drawable.template_white_flipper_active),
                picPainter = painterResource(Res.drawable.pic_flipperscreen_default)
            )
        }
    }
}
