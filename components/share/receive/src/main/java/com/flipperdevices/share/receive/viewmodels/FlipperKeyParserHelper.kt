package com.flipperdevices.share.receive.viewmodels

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.receive.model.FlipperKeyParseException
import javax.inject.Inject

class FlipperKeyParserHelper @Inject constructor(
    private val cryptoStorageApi: CryptoStorageApi
) {
    suspend fun toFlipperKey(link: Deeplink?): Result<FlipperKey> {
        if (link == null) return Result.failure(FlipperKeyParseException())
        if (link !is Deeplink.FlipperKey) return Result.failure(FlipperKeyParseException())

        return when (val content = link.content) {
            is DeeplinkContent.FFFContent -> parseFFFContent(content, link.path)
            is DeeplinkContent.InternalStorageFile -> parseInternalFile(content)
            is DeeplinkContent.FFFSecureContent -> parseSecureLink(content)
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
                FlipperKeyContent.InternalFile(fileKey)
            ),
            synchronized = false,
            deleted = false
        )
        return Result.success(flipperKey)
    }

    private suspend fun parseSecureLink(
        deeplinkContent: DeeplinkContent.FFFSecureContent
    ): Result<FlipperKey> {
        val path = deeplinkContent.filePath
        val name = path.substringAfterLast("/")
        val data = cryptoStorageApi.download(
            id = deeplinkContent.fileId,
            key = deeplinkContent.key,
            name = name
        )
        data.onSuccess {
            val flipperKey = FlipperKey(
                mainFile = FlipperFile(
                    FlipperFilePath(
                        folder = path.substringBeforeLast("/"),
                        nameWithExtension = name
                    ),
                    FlipperKeyContent.RawData(it)
                ),
                synchronized = false,
                deleted = false
            )
            return Result.success(flipperKey)
        }
        data.onFailure {
            return Result.failure(it)
        }
        return Result.failure(FlipperKeyParseException())
    }
}
