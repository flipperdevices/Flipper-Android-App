package com.flipperdevices.bridge.dao.impl.api

import android.net.Uri
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.dao.impl.api.parsers.IButtonParser
import com.flipperdevices.bridge.dao.impl.api.parsers.InfraredParser
import com.flipperdevices.bridge.dao.impl.api.parsers.KeyParserDelegate
import com.flipperdevices.bridge.dao.impl.api.parsers.NFCParser
import com.flipperdevices.bridge.dao.impl.api.parsers.RFIDParser
import com.flipperdevices.bridge.dao.impl.api.parsers.SubGhzParser
import com.flipperdevices.bridge.dao.impl.api.parsers.UnrecognizedParser
import com.flipperdevices.bridge.dao.impl.api.parsers.url.FFFUrlDecoder
import com.flipperdevices.bridge.dao.impl.api.parsers.url.FFFUrlEncoder
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import java.nio.charset.Charset
import java.util.EnumMap
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class, KeyParser::class)
class KeyParserImpl @Inject constructor() : KeyParser, LogTagProvider {
    override val TAG = "KeyParser"

    private val urlDecoder = FFFUrlDecoder()
    private val urlEncoder = FFFUrlEncoder()

    private val parsers by lazy {
        EnumMap<FlipperKeyType, KeyParserDelegate>(
            FlipperKeyType::class.java
        ).apply {
            put(FlipperKeyType.I_BUTTON, IButtonParser())
            put(FlipperKeyType.NFC, NFCParser())
            put(FlipperKeyType.RFID, RFIDParser())
            put(FlipperKeyType.SUB_GHZ, SubGhzParser())
            put(FlipperKeyType.INFRARED, InfraredParser())
        }
    }
    private val unrecognizedParser = UnrecognizedParser()

    override suspend fun parseKey(
        flipperKey: FlipperKey
    ): FlipperKeyParsed = withContext(Dispatchers.IO) {
        val fileContent = flipperKey.keyContent.openStream().use {
            it.readBytes().toString(Charset.defaultCharset())
        }
        val fff = FlipperFileFormat.fromFileContent(fileContent)
        val parser = parsers[flipperKey.path.keyType] ?: unrecognizedParser

        return@withContext parser.parseKey(flipperKey, fff)
    }

    override suspend fun parseUri(uri: Uri): Pair<FlipperFilePath, FlipperFileFormat>? {
        val (path, content) = urlDecoder.uriToContent(uri) ?: return null
        val pathAsFile = File(path)
        val extension = pathAsFile.extension
        val fileType = FlipperKeyType.getByExtension(extension)
        val keyPath = if (fileType == null) {
            warn { "Can't find file type with extension $fileType" }
            FlipperFilePath(pathAsFile.parent ?: "", pathAsFile.name)
        } else FlipperFilePath(fileType.flipperDir, pathAsFile.name)

        return keyPath to content
    }

    override suspend fun keyToUrl(flipperKey: FlipperKey): String {
        val fileContent = flipperKey.keyContent.openStream().use {
            it.readBytes().toString(Charset.defaultCharset())
        }
        val fff = FlipperFileFormat.fromFileContent(fileContent)

        return urlEncoder.keyToUri(flipperKey.path, fff).toString()
    }
}
