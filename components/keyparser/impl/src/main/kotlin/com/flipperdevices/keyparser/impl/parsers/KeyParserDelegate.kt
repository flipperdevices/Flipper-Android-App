package com.flipperdevices.keyparser.impl.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

interface KeyParserDelegate {
    suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed
}
