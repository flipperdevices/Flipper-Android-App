package com.flipperdevices.parser.noop

import android.net.Uri
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, KeyParser::class)
class KeyParserNoop @Inject constructor() : KeyParser {
    override suspend fun parseKey(flipperKey: FlipperKey): FlipperKeyParsed {
        throw NotImplementedError()
    }

    override suspend fun parseUri(uri: Uri): Pair<FlipperFilePath, FlipperFileFormat>? {
        throw NotImplementedError()
    }

    override suspend fun keyToUrl(flipperKey: FlipperKey): String {
        throw NotImplementedError()
    }

    override fun cryptoKeyDataToUri(key: FlipperKeyCrypto): String {
        throw NotImplementedError()
    }

    override fun parseUriToCryptoKeyData(uri: Uri): FlipperKeyCrypto? {
        throw NotImplementedError()
    }
}
