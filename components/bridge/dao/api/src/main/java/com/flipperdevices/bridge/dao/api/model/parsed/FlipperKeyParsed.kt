package com.flipperdevices.bridge.dao.api.model.parsed

import com.flipperdevices.bridge.dao.api.model.FlipperFileType

sealed class FlipperKeyParsed(
    val keyName: String,
    val fileType: FlipperFileType?,
    val notes: String? = null
) {
    class Infrared(
        keyName: String,
        notes: String? = null,
        val protocol: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.INFRARED, notes)

    class NFC(
        keyName: String,
        notes: String? = null,
        val deviceType: String?,
        val uid: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.NFC, notes)

    class SubGhz(
        keyName: String,
        notes: String? = null,
        val protocol: String?,
        val key: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.SUB_GHZ, notes)

    class IButton(
        keyName: String,
        notes: String? = null,
        val keyType: String?,
        val data: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.I_BUTTON, notes)

    class RFID(
        keyName: String,
        notes: String? = null,
        val data: String?,
        val keyType: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.RFID, notes)

    class Unrecognized(keyName: String, fileType: FlipperFileType?, notes: String? = null) :
        FlipperKeyParsed(keyName, fileType, notes)
}
