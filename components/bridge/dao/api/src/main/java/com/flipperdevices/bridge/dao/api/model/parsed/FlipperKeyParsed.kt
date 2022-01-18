package com.flipperdevices.bridge.dao.api.model.parsed

import com.flipperdevices.bridge.dao.api.model.FlipperFileType

sealed class FlipperKeyParsed(
    val keyName: String,
    val fileType: FlipperFileType?,
    val notes: String? = null
) {
    class RFID(
        keyName: String,
        notes: String? = null,
        val data: String?,
        val keyType: String?
    ) : FlipperKeyParsed(keyName, FlipperFileType.RFID, notes)

    class Unrecognized(keyName: String, fileType: FlipperFileType?, notes: String? = null) :
        FlipperKeyParsed(keyName, fileType, notes)
}
