package com.flipperdevices.bridge.dao

import androidx.annotation.DrawableRes

enum class FlipperFileFormat(
    val humanReadableName: String,
    val extension: String,
    val flipperDir: String,
    @DrawableRes val icon: Int
) {
    I_BUTTON("iButton", "ibtn", "ibutton", R.drawable.ic_fileformat_ibutton),
    NFC("NFC", "nfc", "nfc", R.drawable.ic_fileformat_nfc),
    SUB_GHZ("Sub-GHz", "sub", "subghz", R.drawable.ic_fileformat_sub),
    RFID("125kHz RFID", "rfid", "lfrfid", R.drawable.ic_fileformat_rf),
    INFRARED("Infrared", "ir", "irda", R.drawable.ic_fileformat_ir);

    companion object {
        private val extensionToFormat by lazy {
            values().map { it.extension to it }.toMap()
        }

        fun getByExtension(extension: String): FlipperFileFormat? {
            return extensionToFormat[extension]
        }
    }
}
