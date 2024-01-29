package com.flipperdevices.core.ui.flippermockup.internal

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.flippermockup.R
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

internal const val FLIPPER_DEFAULT_HEIGHT = 100f
internal const val FLIPPER_DEFAULT_WIDTH = 238f
internal const val FLIPPER_RATIO = FLIPPER_DEFAULT_WIDTH / FLIPPER_DEFAULT_HEIGHT

@Composable
internal fun ComposableFlipperMockupInternal(
    @DrawableRes templatePicId: Int,
    @DrawableRes picId: Int,
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
            painter = painterResource(templatePicId),
            contentDescription = stringResource(R.string.flippermockup_template_desc)
        )
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(picId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}

@Preview
@Composable
private fun PreviewComposableFlipperMockup() {
    FlipperThemeInternal {
        Column {
            ComposableFlipperMockupInternal(
                modifier = Modifier.fillMaxWidth(),
                templatePicId = R.drawable.template_white_flipper_active,
                picId = R.drawable.pic_flipperscreen_default
            )
        }
    }
}
