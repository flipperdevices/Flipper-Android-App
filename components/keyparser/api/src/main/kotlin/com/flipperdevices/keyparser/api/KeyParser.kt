package com.flipperdevices.keyparser.api

import android.net.Uri
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

interface KeyParser {
    suspend fun parseKey(flipperKey: FlipperKey): FlipperKeyParsed

    suspend fun parseUri(uri: Uri): Pair<FlipperFilePath, FlipperFileFormat>?

    suspend fun keyToUrl(flipperKey: FlipperKey): String

    fun cryptoKeyDataToUri(key: FlipperKeyCrypto): String

    fun parseUriToCryptoKeyData(uri: Uri): FlipperKeyCrypto?
}
