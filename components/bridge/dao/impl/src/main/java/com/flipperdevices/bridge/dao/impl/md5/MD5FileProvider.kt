package com.flipperdevices.bridge.dao.impl.md5

import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import okio.Path

interface MD5FileProvider {
    /**
     * From provided [contentMd5] and [keyContent] returns either
     * new or the file with same md5 and content if already exists
     */
    suspend fun getPathToFile(
        contentMd5: String,
        keyContent: FlipperKeyContent
    ): Path
}
