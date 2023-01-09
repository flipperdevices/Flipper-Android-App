package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
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
