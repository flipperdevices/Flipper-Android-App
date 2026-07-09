package com.flipperdevices.bridge.connection.livetests.tests

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.FeatureNotSupportedError
import javax.inject.Inject

class FileTransferLiveTest @Inject constructor(
    private val featureProvider: FFeatureProvider
) : LiveTest {
    override val name = "File transfer"
    override val description =
        "Upload a small random file to the SD card, verify md5, download it back and compare"

    override suspend fun execute(): Result<String> {
        val storageApi = featureProvider.getSync<FStorageFeatureApi>()
            ?: return Result.failure(FeatureNotSupportedError("STORAGE"))
        return runCatching {
            storageApi.transferAndVerify(FILE_SIZE_BYTES, FLIPPER_FILE_PATH)
        }
    }

    companion object {
        private const val FILE_SIZE_BYTES = 4096
        private const val FLIPPER_FILE_PATH = "/ext/flipper-live-test.bin"
    }
}
