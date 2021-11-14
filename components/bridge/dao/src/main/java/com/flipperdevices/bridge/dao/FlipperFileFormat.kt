package com.flipperdevices.bridge.dao

enum class FlipperFileFormat(
    val extension: String,
    val flipperDir: String
) {
    I_BUTTON("ibtn", "ibutton"),
    NFC("nfc", "nfc"),
    SUB_GHZ("sub", "subghz"),
    RFID("rfid", "lfrfid"),
    INFRARED("ir", "irda"),
}
