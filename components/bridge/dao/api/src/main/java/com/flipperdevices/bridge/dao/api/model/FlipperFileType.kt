package com.flipperdevices.bridge.dao.api.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.flipperdevices.bridge.dao.api.R

enum class FlipperFileType(
    val humanReadableName: String,
    val extension: String,
    val flipperDir: String,
    @DrawableRes val icon: Int,
    @ColorRes val color: Int,
    val flipperAppName: String?
) {
    SUB_GHZ(
        humanReadableName = "Sub-GHz",
        extension = "sub",
        flipperDir = "subghz",
        icon = R.drawable.ic_fileformat_sub,
        color = R.color.fileformat_color_sub,
        flipperAppName = "Sub-GHz"
    ),
    RFID(
        humanReadableName = "RFID 125",
        extension = "rfid",
        flipperDir = "lfrfid",
        icon = R.drawable.ic_fileformat_rf,
        color = R.color.fileformat_color_rf,
        flipperAppName = "125 kHz RFID"
    ),
    NFC(
        humanReadableName = "NFC",
        extension = "nfc",
        flipperDir = "nfc",
        icon = R.drawable.ic_fileformat_nfc,
        color = R.color.fileformat_color_nfc,
        flipperAppName = "NFC"
    ),
    INFRARED(
        humanReadableName = "Infrared",
        extension = "ir",
        flipperDir = "infrared",
        icon = R.drawable.ic_fileformat_ir,
        color = R.color.fileformat_color_ir,
        flipperAppName = "Infrared"
    ),
    I_BUTTON(
        humanReadableName = "iButton",
        extension = "ibtn",
        flipperDir = "ibutton",
        icon = R.drawable.ic_fileformat_ibutton,
        color = R.color.fileformat_color_ibutton,
        flipperAppName = "iButton"
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
