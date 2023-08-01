package com.flipperdevices.core.ui.flippermockup

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

private const val FLIPPER_DEFAULT_HEIGHT = 100f
private const val FLIPPER_DEFAULT_WIDTH = 238f
private const val FLIPPER_RATIO = FLIPPER_DEFAULT_WIDTH / FLIPPER_DEFAULT_HEIGHT
private const val IMAGE_WIDTH_PADDING_PERCENT = 60.56f / FLIPPER_DEFAULT_WIDTH
private const val IMAGE_HEIGHT_PADDING_PERCENT = 10.54f / FLIPPER_DEFAULT_HEIGHT

@Composable
internal fun ComposableFlipperMockupInternal(
    @DrawableRes templatePicId: Int,
    @DrawableRes picId: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
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
        val density = LocalDensity.current
        val picPainter = painterResource(picId)
        val picSize = picPainter.intrinsicSize
        val (width, height) = remember(density, picSize, maxHeight) {
            with(density) {
                val imageRatio = maxHeight.value / FLIPPER_DEFAULT_HEIGHT
                picSize.width.toDp() * imageRatio to picSize.height.toDp() * imageRatio
            }
        }
        Image(
            modifier = Modifier
                .padding(
                    start = maxWidth * IMAGE_WIDTH_PADDING_PERCENT,
                    top = maxHeight * IMAGE_HEIGHT_PADDING_PERCENT
                )
                .size(
                    width = width,
                    height = height
                ),
            painter = picPainter,
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
