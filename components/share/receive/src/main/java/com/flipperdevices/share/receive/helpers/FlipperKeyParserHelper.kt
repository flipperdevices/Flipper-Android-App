package com.flipperdevices.share.receive.helpers

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.receive.models.FlipperKeyParseException
import javax.inject.Inject

class FlipperKeyParserHelper @Inject constructor(
    private val cryptoStorageApi: CryptoStorageApi
) {
    suspend fun toFlipperKey(link: Deeplink?): Result<FlipperKey> {
        if (link == null) return Result.failure(FlipperKeyParseException())
        var content: DeeplinkContent? = null
        var path: FlipperFilePath? = null
        if (link is Deeplink.ExternalContent) {
            content = link.content
        }
        if (link is Deeplink.FlipperKey) {
            path = link.path
            content = link.content
        }

        return when (content) {
            is DeeplinkContent.FFFContent -> parseFFFContent(content, path)
            is DeeplinkContent.InternalStorageFile -> parseInternalFile(content)
            is DeeplinkContent.FFFCryptoContent -> parseCryptoContent(path, content)
            else -> Result.failure(FlipperKeyParseException())
        }
    }

    private fun parseFFFContent(
        deeplinkContent: DeeplinkContent.FFFContent,
        path: FlipperFilePath?
    ): Result<FlipperKey> {
        val localPath = path ?: return Result.failure(FlipperKeyParseException())
        val key = FlipperKey(
            mainFile = FlipperFile(
                localPath,
                deeplinkContent.flipperFileFormat
            ),
            synchronized = false,
            deleted = false
        )
        return Result.success(key)
    }

    private fun parseInternalFile(
        deeplinkContent: DeeplinkContent.InternalStorageFile
    ): Result<FlipperKey> {
        val fileKey = deeplinkContent.file
        val flipperKey = FlipperKey(
            mainFile = FlipperFile(
                FlipperFilePath(
                    folder = fileKey.extension,
                    nameWithExtension = fileKey.name
                ),
                FlipperKeyContent.InternalFile(fileKey.absolutePath)
            ),
            synchronized = false,
            deleted = false
        )
        return Result.success(flipperKey)
    }

    private suspend fun parseCryptoContent(
        path: FlipperFilePath?,
        deeplinkContent: DeeplinkContent.FFFCryptoContent
    ): Result<FlipperKey> {
        val localPath = path ?: return Result.failure(FlipperKeyParseException())
        val flipperKeyContent = cryptoStorageApi.download(
            id = deeplinkContent.key.fileId,
            key = deeplinkContent.key.cryptoKey,
            name = localPath.nameWithExtension
        )
        flipperKeyContent.onSuccess {
            val flipperKey = FlipperKey(
                mainFile = FlipperFile(
                    localPath,
                    it
                ),
                synchronized = false,
                deleted = false
            )
            return Result.success(flipperKey)
        }
        val exception = flipperKeyContent.exceptionOrNull() ?: FlipperKeyParseException()
        return Result.failure(exception)
    }
}
