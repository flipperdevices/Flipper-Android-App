package com.flipperdevices.bridge.dao.api.model

import flipperapp.components.core.ui.res.generated.resources.ic_fileformat_ibutton
import flipperapp.components.core.ui.res.generated.resources.ic_fileformat_ir
import flipperapp.components.core.ui.res.generated.resources.ic_fileformat_nfc
import flipperapp.components.core.ui.res.generated.resources.ic_fileformat_rf
import flipperapp.components.core.ui.res.generated.resources.ic_fileformat_sub
import org.jetbrains.compose.resources.DrawableResource
import flipperapp.components.core.ui.res.generated.resources.Res as DesignSystem

val FlipperKeyType.iconResource: DrawableResource
    get() = when (this) {
        FlipperKeyType.SUB_GHZ -> DesignSystem.drawable.ic_fileformat_sub
        FlipperKeyType.RFID -> DesignSystem.drawable.ic_fileformat_rf
        FlipperKeyType.NFC -> DesignSystem.drawable.ic_fileformat_nfc
        FlipperKeyType.INFRARED -> DesignSystem.drawable.ic_fileformat_ir
        FlipperKeyType.I_BUTTON -> DesignSystem.drawable.ic_fileformat_ibutton
    }
