package com.flipperdevices.core.ui.flippermockup

import androidx.annotation.DrawableRes

enum class ComposableFlipperMockupImage(
    @DrawableRes val imageId: Int
) {
    DEFAULT(R.drawable.pic_flipperscreen_default),
    DEAD(R.drawable.pic_flipperscreen_dead),
    FLASH_FAILED(R.drawable.pic_flipperscreen_flash_failed),
    NFC_READER(R.drawable.pic_flipperscreen_nfc_reader),
    NO_SD(R.drawable.pic_flipperscreen_nosd),
    UPDATING(R.drawable.pic_flipperscreen_updating)
}
