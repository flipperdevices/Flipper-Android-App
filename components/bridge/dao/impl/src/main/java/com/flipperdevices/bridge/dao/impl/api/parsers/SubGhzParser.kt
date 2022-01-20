package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

private const val KEY_PROTOCOL = "Protocol"
private const val KEY_KEY = "Key"

class SubGhzParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        keyContentAsPairs: List<Pair<String, String>>
    ): FlipperKeyParsed {
        val keyContentAsMap = keyContentAsPairs.toMap()

        return FlipperKeyParsed.SubGhz(
            keyName = flipperKey.path.name,
            protocol = keyContentAsMap[KEY_PROTOCOL],
            key = keyContentAsMap[KEY_KEY]
        )
    }
}
