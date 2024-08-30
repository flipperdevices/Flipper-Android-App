package com.flipperdevices.bridge.dao.api.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.theme.LocalPallet

enum class FlipperKeyType(
    val humanReadableName: String,
    val extension: String,
    val flipperDir: String,
    val flipperAppName: String
) {
    SUB_GHZ(
        humanReadableName = "Sub-GHz",
        extension = "sub",
        flipperDir = "subghz",
        flipperAppName = "Sub-GHz"
    ),
    RFID(
        humanReadableName = "RFID 125",
        extension = "rfid",
        flipperDir = "lfrfid",
        flipperAppName = "125 kHz RFID"
    ),
    NFC(
        humanReadableName = "NFC",
        extension = "nfc",
        flipperDir = "nfc",
        flipperAppName = "NFC"
    ),
    INFRARED(
        humanReadableName = "Infrared",
        extension = "ir",
        flipperDir = "infrared",
        flipperAppName = "Infrared"
    ),
    I_BUTTON(
        humanReadableName = "iButton",
        extension = "ibtn",
        flipperDir = "ibutton",
        flipperAppName = "iButton"
    );

    companion object {
        private val extensionToFormat by lazy {
            values().associateBy { it.extension }
        }

        fun getByExtension(extension: String): FlipperKeyType? {
            return extensionToFormat[extension]
        }

        @Composable
        fun colorByFlipperKeyType(type: FlipperKeyType?): Color {
            return when (type) {
                SUB_GHZ -> LocalPallet.current.keySubGHz
                RFID -> LocalPallet.current.keyRFID
                NFC -> LocalPallet.current.keyNFC
                INFRARED -> LocalPallet.current.keyInfrared
                I_BUTTON -> LocalPallet.current.keyIButton
                else -> LocalPallet.current.keyUnknown
            }
        }
    }
}
