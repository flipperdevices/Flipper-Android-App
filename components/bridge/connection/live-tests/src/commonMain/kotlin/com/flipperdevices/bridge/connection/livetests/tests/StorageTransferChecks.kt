package com.flipperdevices.bridge.connection.livetests.tests

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.livetests.model.LiveTestAssertionError
import kotlinx.coroutines.coroutineScope
import okio.ByteString.Companion.toByteString
import okio.buffer
import okio.use
import kotlin.random.Random

/**
 * Uploads [payloadSizeBytes] random bytes to [flipperFilePath], verifies the md5
 * on the device, downloads the file back, compares content and deletes the file.
 */
internal suspend fun FStorageFeatureApi.transferAndVerify(
    payloadSizeBytes: Int,
    flipperFilePath: String
): String {
    val payload = Random.nextBytes(payloadSizeBytes)
    uploadApi().sink(flipperFilePath).buffer().use { bufferedSink ->
        bufferedSink.write(payload)
    }

    val expectedMd5 = payload.toByteString().md5().hex()
    val flipperMd5 = md5Api().md5(flipperFilePath).getOrThrow()
    if (flipperMd5 != expectedMd5) {
        throw LiveTestAssertionError(
            "MD5 mismatch after upload: expected $expectedMd5, flipper reports $flipperMd5"
        )
    }

    val downloadedPayload = coroutineScope {
        downloadApi()
            .source(flipperFilePath, this)
            .buffer()
            .use { bufferedSource -> bufferedSource.readByteArray() }
    }
    if (!downloadedPayload.contentEquals(payload)) {
        throw LiveTestAssertionError(
            "Downloaded content differs from uploaded " +
                "(${downloadedPayload.size} vs ${payload.size} bytes)"
        )
    }

    deleteApi().delete(flipperFilePath).getOrThrow()
    return "Transferred and verified ${payload.size} bytes (md5 $expectedMd5)"
}
