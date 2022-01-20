package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

private const val KEY_TYPE = "Key type"
private const val KEY_DATA = "Data"

class IButtonParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        keyContentAsPairs: List<Pair<String, String>>
    ): FlipperKeyParsed {
        val keyContentAsMap = keyContentAsPairs.toMap()

        return FlipperKeyParsed.IButton(
            keyName = flipperKey.path.name,
            data = keyContentAsMap[KEY_DATA],
            keyType = keyContentAsMap[KEY_TYPE]
        )
    }
}
