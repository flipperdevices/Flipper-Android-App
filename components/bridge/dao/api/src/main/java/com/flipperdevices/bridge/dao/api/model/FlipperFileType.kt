package com.flipperdevices.bridge.dao.api.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.LocalPallet

enum class FlipperFileType(
    val humanReadableName: String,
    val extension: String,
    val flipperDir: String,
    @DrawableRes val icon: Int,
    val flipperAppName: String?
) {
    SUB_GHZ(
        humanReadableName = "Sub-GHz",
        extension = "sub",
        flipperDir = "subghz",
        icon = R.drawable.ic_fileformat_sub,
        flipperAppName = "Sub-GHz"
    ),
    RFID(
        humanReadableName = "RFID 125",
        extension = "rfid",
        flipperDir = "lfrfid",
        icon = R.drawable.ic_fileformat_rf,
        flipperAppName = "125 kHz RFID"
    ),
    NFC(
        humanReadableName = "NFC",
        extension = "nfc",
        flipperDir = "nfc",
        icon = R.drawable.ic_fileformat_nfc,
        flipperAppName = "NFC"
    ),
    INFRARED(
        humanReadableName = "Infrared",
        extension = "ir",
        flipperDir = "infrared",
        icon = R.drawable.ic_fileformat_ir,
        flipperAppName = "Infrared"
    ),
    I_BUTTON(
        humanReadableName = "iButton",
        extension = "ibtn",
        flipperDir = "ibutton",
        icon = R.drawable.ic_fileformat_ibutton,
        flipperAppName = "iButton"
    );

    companion object {
        private val extensionToFormat by lazy {
            values().map { it.extension to it }.toMap()
        }

        fun getByExtension(extension: String): FlipperFileType? {
            return extensionToFormat[extension]
        }

        @Composable
        fun colorByFlipperFileType(type: FlipperFileType?): Color {
            return when (type) {
                SUB_GHZ -> LocalPallet.current.keySubGHz
                RFID -> LocalPallet.current.keyRFID
                NFC -> LocalPallet.current.keyNFC
                INFRARED -> LocalPallet.current.keyInfrarred
                I_BUTTON -> LocalPallet.current.keyIButton
                else -> LocalPallet.current.keyUnknown
            }
        }
    }
}
