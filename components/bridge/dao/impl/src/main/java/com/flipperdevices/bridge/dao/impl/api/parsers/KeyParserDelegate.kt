package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

internal interface KeyParserDelegate {
    suspend fun parseKey(flipperKey: FlipperKey): FlipperKeyParsed
}
