package com.flipperdevices.keyparser.impl.parsers.impl

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.KeyParserDelegate

private const val KEY_NAME = "name"
private const val KEY_PROTOCOL = "protocol"

class InfraredParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        val keyDict = fff.orderedDict
        val keyContentAsMap = keyDict.toMap()

        val protocol = keyContentAsMap[KEY_PROTOCOL]
        val remotes = keyDict.filter { it.first == KEY_NAME }.map { it.second }

        return FlipperKeyParsed.Infrared(
            keyName = flipperKey.path.nameWithoutExtension,
            notes = flipperKey.notes,
            protocol = protocol,
            remotes = remotes
        )
    }
}
