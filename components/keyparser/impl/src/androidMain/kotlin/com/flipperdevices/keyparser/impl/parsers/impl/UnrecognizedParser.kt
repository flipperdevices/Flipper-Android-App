package com.flipperdevices.keyparser.impl.parsers.impl

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.KeyParserDelegate
import kotlinx.collections.immutable.toImmutableList

class UnrecognizedParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        return FlipperKeyParsed.Unrecognized(
            flipperKey.path.nameWithoutExtension,
            flipperKey.notes,
            flipperKey.path.keyType,
            fff.orderedDict.toImmutableList()
        )
    }
}
