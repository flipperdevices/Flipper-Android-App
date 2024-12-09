package com.flipperdevices.nfc.mfkey32.api

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileStorageMD5Api
import kotlinx.coroutines.flow.Flow

interface MfKey32Api {
    /**
     * Cached value of bruteforce file existing
     *
     * This should be updated each [checkBruteforceFileExist]
     */
    val isBruteforceFileExist: Boolean

    fun hasNotification(): Flow<Boolean>

    @Deprecated("Use new api")
    suspend fun checkBruteforceFileExist(requestApi: FlipperRequestApi)

    suspend fun checkBruteforceFileExist(md5StorageApi: FFileStorageMD5Api)
}
