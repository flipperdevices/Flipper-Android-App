package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

private const val KEY_PROTOCOL = "protocol"

class InfraredParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        keyContentAsPairs: List<Pair<String, String>>
    ): FlipperKeyParsed {
        val keyContentAsMap = keyContentAsPairs.toMap()

        return FlipperKeyParsed.Infrared(
            keyName = flipperKey.path.name,
            protocol = keyContentAsMap[KEY_PROTOCOL]
        )
    }
}
