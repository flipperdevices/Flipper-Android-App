package com.flipperdevices.bridge.dao.noop

import android.net.Uri
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, KeyParser::class)
class KeyParserNoop @Inject constructor() : KeyParser {
    override suspend fun parseKey(flipperKey: FlipperKey): FlipperKeyParsed {
        error("has no stub yet")
    }

    override suspend fun parseUri(uri: Uri): Pair<FlipperFilePath, FlipperFileFormat>? {
        error("has no stub yet")
    }

    override suspend fun keyToUrl(flipperKey: FlipperKey): String {
        error("has no stub yet")
    }

    override fun cryptoKeyDataToUri(key: FlipperKeyCrypto): String {
        error("has no stub yet")
    }

    override fun parseUriToCryptoKeyData(uri: Uri): FlipperKeyCrypto? {
        error("has no stub yet")
    }
}
