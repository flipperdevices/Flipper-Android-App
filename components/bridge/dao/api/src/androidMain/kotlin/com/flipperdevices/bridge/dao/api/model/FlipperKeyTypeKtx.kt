package com.flipperdevices.bridge.dao.api.model

import com.flipperdevices.core.ui.res.R as DesignSystem

val FlipperKeyType.icon: Int
    get() = when (this) {
        FlipperKeyType.SUB_GHZ -> DesignSystem.drawable.ic_fileformat_sub
        FlipperKeyType.RFID -> DesignSystem.drawable.ic_fileformat_rf
        FlipperKeyType.NFC -> DesignSystem.drawable.ic_fileformat_nfc
        FlipperKeyType.INFRARED -> DesignSystem.drawable.ic_fileformat_ir
        FlipperKeyType.I_BUTTON -> DesignSystem.drawable.ic_fileformat_ibutton
    }
