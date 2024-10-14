package com.flipperdevices.core.ui.flippermockup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.flippermockup.internal.ComposableFlipperMockupInternal
import com.flipperdevices.core.ui.flippermockup.internal.ComposableFlipperMockupInternalRaw
import flipperapp.components.core.ui.flippermockup.generated.resources.Res
import flipperapp.components.core.ui.flippermockup.generated.resources.template_black_flipper_active
import flipperapp.components.core.ui.flippermockup.generated.resources.template_black_flipper_disabled
import flipperapp.components.core.ui.flippermockup.generated.resources.template_transparent_flipper_active
import flipperapp.components.core.ui.flippermockup.generated.resources.template_transparent_flipper_disabled
import flipperapp.components.core.ui.flippermockup.generated.resources.template_white_flipper_active
import flipperapp.components.core.ui.flippermockup.generated.resources.template_white_flipper_disabled
import org.jetbrains.compose.resources.painterResource

@Composable
fun ComposableFlipperMockup(
    flipperColor: HardwareColor,
    isActive: Boolean,
    mockupImage: ComposableFlipperMockupImage,
    modifier: Modifier = Modifier
) {
    val templatePicResource = getTemplatePicId(flipperColor, isActive)

    ComposableFlipperMockupInternal(
        templatePicPainter = painterResource(templatePicResource),
        picPainter = painterResource(mockupImage.imageResource),
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
    val templatePicResource = getTemplatePicId(flipperColor, isActive)

    ComposableFlipperMockupInternalRaw(
        templatePic = painterResource(templatePicResource),
        modifier = modifier,
        content = content
    )
}

private fun getTemplatePicId(color: HardwareColor, isActive: Boolean) = when (color) {
    is HardwareColor.Unrecognized,
    HardwareColor.WHITE -> when (isActive) {
        true -> Res.drawable.template_white_flipper_active
        false -> Res.drawable.template_white_flipper_disabled
    }

    HardwareColor.BLACK -> when (isActive) {
        true -> Res.drawable.template_black_flipper_active
        false -> Res.drawable.template_black_flipper_disabled
    }

    HardwareColor.TRANSPARENT -> when (isActive) {
        true -> Res.drawable.template_transparent_flipper_active
        false -> Res.drawable.template_transparent_flipper_disabled
    }
}
