package com.flipperdevices.bridge.dao

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

enum class FlipperFileType(
    val humanReadableName: String,
    val extension: String,
    val flipperDir: String,
    @DrawableRes val icon: Int,
    @ColorRes val color: Int
) {
    I_BUTTON(
        humanReadableName = "iButton",
        extension = "ibtn",
        flipperDir = "ibutton",
        icon = R.drawable.ic_fileformat_ibutton,
        color = R.color.fileformat_color_ibutton
    ),
    NFC(
        humanReadableName = "NFC",
        extension = "nfc",
        flipperDir = "nfc",
        icon = R.drawable.ic_fileformat_nfc,
        color = R.color.fileformat_color_nfc
    ),
    SUB_GHZ(
        humanReadableName = "Sub-GHz",
        extension = "sub",
        flipperDir = "subghz/saved",
        icon = R.drawable.ic_fileformat_sub,
        color = R.color.fileformat_color_sub
    ),
    RFID(
        humanReadableName = "RFID125",
        extension = "rfid",
        flipperDir = "lfrfid",
        icon = R.drawable.ic_fileformat_rf,
        color = R.color.fileformat_color_rf
    ),
    INFRARED(
        humanReadableName = "Infrared",
        extension = "ir",
        flipperDir = "irda",
        icon = R.drawable.ic_fileformat_ir,
        color = R.color.fileformat_color_ir
    );

    companion object {
        private val extensionToFormat by lazy {
            values().map { it.extension to it }.toMap()
        }

        fun getByExtension(extension: String): FlipperFileType? {
            return extensionToFormat[extension]
        }
    }
}
