package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.dao.impl.api.parsers.IButtonParser
import com.flipperdevices.bridge.dao.impl.api.parsers.InfraredParser
import com.flipperdevices.bridge.dao.impl.api.parsers.KeyParserDelegate
import com.flipperdevices.bridge.dao.impl.api.parsers.NFCParser
import com.flipperdevices.bridge.dao.impl.api.parsers.RFIDParser
import com.flipperdevices.bridge.dao.impl.api.parsers.SubGhzParser
import com.flipperdevices.bridge.dao.impl.api.parsers.UnrecognizedParser
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import java.nio.charset.Charset
import java.util.EnumMap
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class)
class KeyParserImpl @Inject constructor() : KeyParser {
    private val parsers by lazy {
        EnumMap<FlipperFileType, KeyParserDelegate>(
            FlipperFileType::class.java
        ).apply {
            put(FlipperFileType.I_BUTTON, IButtonParser())
            put(FlipperFileType.NFC, NFCParser())
            put(FlipperFileType.RFID, RFIDParser())
            put(FlipperFileType.SUB_GHZ, SubGhzParser())
            put(FlipperFileType.INFRARED, InfraredParser())
        }
    }
    private val unrecognizedParser = UnrecognizedParser()

    override suspend fun parseKey(
        flipperKey: FlipperKey
    ): FlipperKeyParsed = withContext(Dispatchers.IO) {
        val parser = parsers[flipperKey.path.fileType] ?: unrecognizedParser
        val fileContent = flipperKey.keyContent.stream().use {
            it.readBytes().toString(Charset.defaultCharset())
        }

        return@withContext parser.parseKey(
            flipperKey,
            FlipperFileFormat.fromFileContent(fileContent)
        )
    }
}
