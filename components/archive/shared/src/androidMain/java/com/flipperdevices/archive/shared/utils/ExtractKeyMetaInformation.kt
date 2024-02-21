package com.flipperdevices.archive.shared.utils

import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

object ExtractKeyMetaInformation {
    fun extractProtocol(keyParsed: FlipperKeyParsed): String? {
        return when (keyParsed) {
            is FlipperKeyParsed.IButton -> keyParsed.keyType
            is FlipperKeyParsed.Infrared -> keyParsed.protocol
            is FlipperKeyParsed.NFC -> keyParsed.deviceType
            is FlipperKeyParsed.RFID -> keyParsed.keyType
            is FlipperKeyParsed.SubGhz -> keyParsed.protocol
            is FlipperKeyParsed.Unrecognized -> null
        }
    }
}
