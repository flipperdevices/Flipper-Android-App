package com.flipperdevices.keyparser.impl.parsers.impl

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.KeyParserDelegate

private const val KEY_PROTOCOL = "protocol"
class InfraredParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        val keyContentAsMap = fff.orderedDict.toMap()

        return FlipperKeyParsed.Infrared(
            keyName = flipperKey.path.nameWithoutExtension,
            notes = flipperKey.notes,
            protocol = keyContentAsMap[KEY_PROTOCOL]
        )
    }
}
