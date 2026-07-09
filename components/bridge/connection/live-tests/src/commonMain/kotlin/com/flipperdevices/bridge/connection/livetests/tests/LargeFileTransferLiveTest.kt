package com.flipperdevices.bridge.connection.livetests.tests

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.FeatureNotSupportedError
import javax.inject.Inject

class LargeFileTransferLiveTest @Inject constructor(
    private val featureProvider: FFeatureProvider
) : LiveTest {
    override val name = "Large file transfer"
    override val description =
        "Upload a 64 KiB random file to stress chunked reads/writes, verify md5 and content"

    override suspend fun execute(): Result<String> {
        val storageApi = featureProvider.getSync<FStorageFeatureApi>()
            ?: return Result.failure(FeatureNotSupportedError("STORAGE"))
        return runCatching {
            storageApi.transferAndVerify(FILE_SIZE_BYTES, FLIPPER_FILE_PATH)
        }
    }

    companion object {
        private const val FILE_SIZE_BYTES = 65536
        private const val FLIPPER_FILE_PATH = "/ext/flipper-live-test-large.bin"
    }
}
