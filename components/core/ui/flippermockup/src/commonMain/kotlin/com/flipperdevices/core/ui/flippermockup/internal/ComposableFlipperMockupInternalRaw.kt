package com.flipperdevices.core.ui.flippermockup.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import flipperapp.components.core.ui.flippermockup.generated.resources.Res
import flipperapp.components.core.ui.flippermockup.generated.resources.flippermockup_template_desc
import org.jetbrains.compose.resources.stringResource

private const val IMAGE_WIDTH_PADDING_PERCENT = 60.56f / FLIPPER_DEFAULT_WIDTH
private const val IMAGE_HEIGHT_PADDING_PERCENT = 10.54f / FLIPPER_DEFAULT_HEIGHT
private const val IMAGE_WIDTH_PERCENT = 85.33f / FLIPPER_DEFAULT_WIDTH
private const val IMAGE_HEIGHT_PERCENT = 46.96f / FLIPPER_DEFAULT_HEIGHT
private const val IMAGE_ROUND_CORNER_PERCENT = 3.4f / FLIPPER_DEFAULT_WIDTH

@Composable
fun ComposableFlipperMockupInternalRaw(
    templatePic: Painter,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier
            .aspectRatio(
                ratio = FLIPPER_RATIO
            )
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = templatePic,
            contentDescription = stringResource(Res.string.flippermockup_template_desc)
        )
        Box(
            modifier = Modifier
                .padding(
                    start = remember(maxWidth) { maxWidth * IMAGE_WIDTH_PADDING_PERCENT },
                    top = remember(maxHeight) { maxHeight * IMAGE_HEIGHT_PADDING_PERCENT }
                )
                .size(
                    width = remember(maxWidth) { maxWidth * IMAGE_WIDTH_PERCENT },
                    height = remember(maxHeight) { maxHeight * IMAGE_HEIGHT_PERCENT }
                )
                .clip(
                    RoundedCornerShape(
                        size = remember(maxWidth) { maxWidth * IMAGE_ROUND_CORNER_PERCENT }
                    )
                ),
        ) {
            content()
        }
    }
}
