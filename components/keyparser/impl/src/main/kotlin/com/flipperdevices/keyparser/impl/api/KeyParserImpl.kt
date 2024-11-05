package com.flipperdevices.keyparser.impl.api

import android.net.Uri
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.data.PredefinedEnumMap
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.impl.IButtonParser
import com.flipperdevices.keyparser.impl.parsers.impl.InfraredParser
import com.flipperdevices.keyparser.impl.parsers.impl.NFCParser
import com.flipperdevices.keyparser.impl.parsers.impl.RFIDParser
import com.flipperdevices.keyparser.impl.parsers.impl.SubGhzParser
import com.flipperdevices.keyparser.impl.parsers.impl.UnrecognizedParser
import com.flipperdevices.keyparser.impl.parsers.url.FFFUrlDecoder
import com.flipperdevices.keyparser.impl.parsers.url.FFFUrlEncoder
import com.flipperdevices.keyparser.impl.parsers.url.PATH_FOR_FFF_CRYPTO_LIHK
import com.flipperdevices.keyparser.impl.parsers.url.PREFFERED_HOST
import com.flipperdevices.keyparser.impl.parsers.url.PREFFERED_SCHEME
import com.flipperdevices.keyparser.impl.parsers.url.QUERY_ID
import com.flipperdevices.keyparser.impl.parsers.url.QUERY_KEY
import com.flipperdevices.keyparser.impl.parsers.url.QUERY_KEY_PATH
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.nio.charset.Charset
import javax.inject.Inject

@ContributesBinding(AppGraph::class, KeyParser::class)
class KeyParserImpl @Inject constructor() : KeyParser, LogTagProvider {
    override val TAG = "KeyParser"

    private val urlDecoder = FFFUrlDecoder()
    private val urlEncoder = FFFUrlEncoder()

    private val parsers = PredefinedEnumMap(FlipperKeyType::class.java) {
        when (it) {
            FlipperKeyType.NFC -> NFCParser()
            FlipperKeyType.INFRARED -> InfraredParser()
            FlipperKeyType.RFID -> RFIDParser()
            FlipperKeyType.SUB_GHZ -> SubGhzParser()
            FlipperKeyType.I_BUTTON -> IButtonParser()
        }
    }
    private val unrecognizedParser = UnrecognizedParser()

    override suspend fun parseKey(
        flipperKey: FlipperKey
    ): FlipperKeyParsed = withContext(FlipperDispatchers.workStealingDispatcher) {
        val fileContent = flipperKey.keyContent.openStream().use {
            it.readBytes().toString(Charset.defaultCharset())
        }
        val fff = FlipperFileFormat.fromFileContent(fileContent)

        val flipperType = flipperKey.flipperKeyType
            ?: return@withContext unrecognizedParser.parseKey(flipperKey, fff)

        val parser = parsers[flipperType]

        return@withContext parser.parseKey(flipperKey, fff)
    }

    override suspend fun parseUri(uri: Uri): Pair<FlipperFilePath, FlipperFileFormat>? {
        val (path, content) = urlDecoder.uriToContent(uri) ?: return null
        val pathAsFile = File(path)
        val extension = pathAsFile.extension
        val fileType = FlipperKeyType.getByExtension(extension)
        val keyPath = if (fileType == null) {
            warn { "Can't find file type with extension $fileType" }
            FlipperFilePath(pathAsFile.parent.orEmpty(), pathAsFile.name)
        } else {
            FlipperFilePath(fileType.flipperDir, pathAsFile.name)
        }

        return keyPath to content
    }

    override suspend fun keyToUrl(flipperKey: FlipperKey): String {
        val fileContent = flipperKey.keyContent.openStream().use {
            it.readBytes().toString(Charset.defaultCharset())
        }
        val fff = FlipperFileFormat.fromFileContent(fileContent)

        return urlEncoder.keyToUri(flipperKey.path, fff).toString()
    }

    override fun cryptoKeyDataToUri(key: FlipperKeyCrypto): String {
        val query = urlEncoder.encodeQuery(
            listOf(
                QUERY_KEY_PATH to key.pathToKey,
                QUERY_KEY to key.cryptoKey,
                QUERY_ID to key.fileId
            )
        )
        return URL(
            PREFFERED_SCHEME,
            PREFFERED_HOST,
            "$PATH_FOR_FFF_CRYPTO_LIHK#$query"
        ).toString()
    }

    override fun parseUriToCryptoKeyData(uri: Uri): FlipperKeyCrypto? {
        val fragment = uri.encodedFragment
        if (fragment == null) {
            info { "Failed parse $uri because fragment is null" }
            return null
        }
        val parsedFragment = urlDecoder.decodeQuery(fragment)

        val path = parsedFragment
            .firstOrNull { it.first == QUERY_KEY_PATH }
            ?.second

        val key = parsedFragment
            .firstOrNull { it.first == QUERY_KEY }
            ?.second

        val fileId = parsedFragment
            .firstOrNull { it.first == QUERY_ID }
            ?.second

        if (path == null || key == null || fileId == null) {
            info { "Failed parse uri $uri because path, key or fileId is null ($path,$key,$fileId)" }
            return null
        }

        return FlipperKeyCrypto(
            fileId = fileId,
            cryptoKey = key,
            pathToKey = path
        )
    }
}
