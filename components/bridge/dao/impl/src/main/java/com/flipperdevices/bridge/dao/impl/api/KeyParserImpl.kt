package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.dao.impl.api.parsers.KeyParserDelegate
import com.flipperdevices.bridge.dao.impl.api.parsers.RFIDParser
import com.flipperdevices.bridge.dao.impl.api.parsers.UnrecognizedParser
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import java.util.EnumMap
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyParserImpl @Inject constructor() : KeyParser {
    private val parsers = EnumMap<FlipperFileType, KeyParserDelegate>(
        FlipperFileType::class.java
    ).apply {
        put(FlipperFileType.RFID, RFIDParser())
    }
    private val unrecognizedParser = UnrecognizedParser()

    override suspend fun parseKey(flipperKey: FlipperKey): FlipperKeyParsed {
        val parser = parsers[flipperKey.path.fileType] ?: unrecognizedParser
        return parser.parseKey(flipperKey)
    }
}
