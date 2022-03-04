package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

private const val KEY_PROTOCOL = "Protocol"
private const val KEY_KEY = "Key"

class SubGhzParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        val keyContentAsMap = fff.orderedDict.toMap()

        return FlipperKeyParsed.SubGhz(
            keyName = flipperKey.path.nameWithoutExtension,
            notes = flipperKey.notes,
            protocol = keyContentAsMap[KEY_PROTOCOL],
            key = keyContentAsMap[KEY_KEY]
        )
    }
}
