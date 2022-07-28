package com.flipperdevices.bridge.dao.api.model.parsed

import com.flipperdevices.bridge.dao.api.model.FlipperFileType

sealed class FlipperKeyParsed(
    val keyName: String,
    val notes: String?,
    val fileType: FlipperFileType?
) {
    class Infrared(
        keyName: String,
        notes: String?,
        val protocol: String?
    ) : FlipperKeyParsed(keyName, notes, FlipperFileType.INFRARED)

    @Suppress("LongParameterList")
    class NFC(
        keyName: String,
        notes: String?,
        val deviceType: String?,
        val uid: String?,
        val version: Int,
        val atqa: String?,
        val sak: String?,
        val mifareClassicType: String?,
        val dataFormatVersion: Int,
        val lines: List<Pair<Int, String>>
    ) : FlipperKeyParsed(keyName, notes, FlipperFileType.NFC)

    class SubGhz(
        keyName: String,
        notes: String?,
        val protocol: String?,
        val key: String?
    ) : FlipperKeyParsed(keyName, notes, FlipperFileType.SUB_GHZ)

    class IButton(
        keyName: String,
        notes: String?,
        val keyType: String?,
        val data: String?
    ) : FlipperKeyParsed(keyName, notes, FlipperFileType.I_BUTTON)

    class RFID(
        keyName: String,
        notes: String?,
        val data: String?,
        val keyType: String?
    ) : FlipperKeyParsed(keyName, notes, FlipperFileType.RFID)

    class Unrecognized(
        keyName: String,
        notes: String?,
        fileType: FlipperFileType?,
        val orderedDict: List<Pair<String, String>>
    ) : FlipperKeyParsed(keyName, notes, fileType)
}
