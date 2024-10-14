package com.flipperdevices.core.ui.flippermockup.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import flipperapp.components.core.ui.flippermockup.generated.resources.Res
import flipperapp.components.core.ui.flippermockup.generated.resources.flippermockup_template_desc
import org.jetbrains.compose.resources.stringResource

internal const val FLIPPER_DEFAULT_HEIGHT = 100f
internal const val FLIPPER_DEFAULT_WIDTH = 238f
internal const val FLIPPER_RATIO = FLIPPER_DEFAULT_WIDTH / FLIPPER_DEFAULT_HEIGHT

@Composable
internal fun ComposableFlipperMockupInternal(
    templatePicPainter: Painter,
    picPainter: Painter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .aspectRatio(
                ratio = FLIPPER_RATIO
            )
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = templatePicPainter,
            contentDescription = stringResource(Res.string.flippermockup_template_desc)
        )
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = picPainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}
