package com.flipperdevices.faphub.utils

import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import javax.inject.Inject

private const val FLIPPER_TMP_FOLDER_PATH = "/ext/.tmp/android"

class FapHubTmpFolderProvider @Inject constructor(
    private val flipperStorageApi: FlipperStorageApi
) {
    suspend fun provideTmpFolder(): String {
        flipperStorageApi.mkdirs(FLIPPER_TMP_FOLDER_PATH)
        return FLIPPER_TMP_FOLDER_PATH
    }
}
