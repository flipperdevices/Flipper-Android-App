package com.flipperdevices.keyparser.api.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import kotlinx.collections.immutable.ImmutableList

sealed class FlipperKeyParsed(
    val keyName: String,
    val notes: String?,
    val fileType: FlipperKeyType?
) {
    class Infrared(
        keyName: String,
        notes: String?,
        val protocol: String?,
        val remotes: List<String>
    ) : FlipperKeyParsed(keyName, notes, FlipperKeyType.INFRARED)

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
    ) : FlipperKeyParsed(keyName, notes, FlipperKeyType.NFC)

    class SubGhz(
        keyName: String,
        notes: String?,
        val protocol: String?,
        val key: String?,
        val totalTimeMs: Long? = null
    ) : FlipperKeyParsed(keyName, notes, FlipperKeyType.SUB_GHZ)

    class IButton(
        keyName: String,
        notes: String?,
        val keyType: String?,
        val data: String?
    ) : FlipperKeyParsed(keyName, notes, FlipperKeyType.I_BUTTON)

    class RFID(
        keyName: String,
        notes: String?,
        val data: String?,
        val keyType: String?
    ) : FlipperKeyParsed(keyName, notes, FlipperKeyType.RFID)

    class Unrecognized(
        keyName: String,
        notes: String?,
        fileType: FlipperKeyType?,
        val orderedDict: ImmutableList<Pair<String, String>>
    ) : FlipperKeyParsed(keyName, notes, fileType)
}
