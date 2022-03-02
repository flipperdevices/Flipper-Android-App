package com.flipperdevices.bridge.dao.api.model.parsed

import com.flipperdevices.bridge.dao.api.model.FlipperFileType

sealed class FlipperKeyParsed(
    val keyName: String,
    val fileType: FlipperFileType?
) {
    class Infrared(
        keyName: String,
        val protocol: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.INFRARED)

    class NFC(
        keyName: String,
        val deviceType: String?,
        val uid: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.NFC)

    class SubGhz(
        keyName: String,
        val protocol: String?,
        val key: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.SUB_GHZ)

    class IButton(
        keyName: String,
        val keyType: String?,
        val data: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.I_BUTTON)

    class RFID(
        keyName: String,
        val data: String?,
        val keyType: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.RFID)

    class Unrecognized(
        keyName: String,
        fileType: FlipperFileType?,
        val orderedDict: List<Pair<String, String>>
    ) : FlipperKeyParsed(keyName, fileType)
}
