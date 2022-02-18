package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

private const val KEY_TYPE = "Device type"
private const val KEY_UID = "UID"

class NFCParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        val keyContentAsMap = fff.orderedDict.toMap()

        return FlipperKeyParsed.NFC(
            keyName = flipperKey.path.name,
            deviceType = keyContentAsMap[KEY_TYPE],
            uid = keyContentAsMap[KEY_UID]
        )
    }
}
