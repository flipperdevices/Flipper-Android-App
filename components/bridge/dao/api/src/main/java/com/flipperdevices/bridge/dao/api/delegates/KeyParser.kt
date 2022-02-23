package com.flipperdevices.bridge.dao.api.delegates

import android.net.Uri
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

interface KeyParser {
    /**
     * Load key content and map it to object
     */
    suspend fun parseKey(flipperKey: FlipperKey): FlipperKeyParsed

    suspend fun parseUri(uri: Uri): Pair<FlipperKeyPath, FlipperFileFormat>?

    suspend fun keyToUrl(flipperKey: FlipperKey): String
}
