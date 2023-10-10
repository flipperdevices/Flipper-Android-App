package com.flipperdevices.core.ui.flippermockup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.flippermockup.internal.ComposableFlipperMockupInternal
import com.flipperdevices.core.ui.flippermockup.internal.ComposableFlipperMockupInternalRaw
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Composable
fun ComposableFlipperMockup(
    flipperColor: HardwareColor,
    isActive: Boolean,
    mockupImage: ComposableFlipperMockupImage,
    modifier: Modifier = Modifier
) {
    val templatePicId = getTemplatePicId(flipperColor, isActive)

    ComposableFlipperMockupInternal(
        templatePicId = templatePicId,
        picId = mockupImage.imageId,
        modifier = modifier
    )
}

@Composable
fun ComposableFlipperMockup(
    flipperColor: HardwareColor,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val templatePicId = getTemplatePicId(flipperColor, isActive)

    ComposableFlipperMockupInternalRaw(
        templatePicId = templatePicId,
        modifier = modifier,
        content = content
    )
}

private fun getTemplatePicId(color: HardwareColor, isActive: Boolean) = when (color) {
    HardwareColor.UNRECOGNIZED,
    HardwareColor.WHITE -> when (isActive) {
        true -> R.drawable.template_white_flipper_active
        false -> R.drawable.template_white_flipper_disabled
    }

    HardwareColor.BLACK -> when (isActive) {
        true -> R.drawable.template_black_flipper_active
        false -> R.drawable.template_black_flipper_disabled
    }

    HardwareColor.TRANSPARENT -> when (isActive) {
        true -> R.drawable.template_transparent_flipper_active
        false -> R.drawable.template_transparent_flipper_disabled
    }
}

@Preview(
    showBackground = true,
    heightDp = 1500
)
@Composable
private fun PreviewComposableFlipperMockup() {
    FlipperThemeInternal {
        Column {
            HardwareColor.values().forEach { color ->
                listOf(true, false).forEach { isActive ->
                    ComposableFlipperMockup(
                        flipperColor = color,
                        isActive = isActive,
                        mockupImage = ComposableFlipperMockupImage.DEFAULT,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
