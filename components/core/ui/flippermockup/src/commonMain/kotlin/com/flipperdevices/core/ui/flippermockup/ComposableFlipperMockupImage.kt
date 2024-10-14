package com.flipperdevices.core.ui.flippermockup

import flipperapp.components.core.ui.flippermockup.generated.resources.Res
import flipperapp.components.core.ui.flippermockup.generated.resources.pic_flipperscreen_dead
import flipperapp.components.core.ui.flippermockup.generated.resources.pic_flipperscreen_default
import flipperapp.components.core.ui.flippermockup.generated.resources.pic_flipperscreen_flash_failed
import flipperapp.components.core.ui.flippermockup.generated.resources.pic_flipperscreen_nfc_reader
import flipperapp.components.core.ui.flippermockup.generated.resources.pic_flipperscreen_nosd
import flipperapp.components.core.ui.flippermockup.generated.resources.pic_flipperscreen_updating
import org.jetbrains.compose.resources.DrawableResource

enum class ComposableFlipperMockupImage(
    val imageResource: DrawableResource
) {
    DEFAULT(Res.drawable.pic_flipperscreen_default),
    DEAD(Res.drawable.pic_flipperscreen_dead),
    FLASH_FAILED(Res.drawable.pic_flipperscreen_flash_failed),
    NFC_READER(Res.drawable.pic_flipperscreen_nfc_reader),
    NO_SD(Res.drawable.pic_flipperscreen_nosd),
    UPDATING(Res.drawable.pic_flipperscreen_updating)
}
