package com.flipperdevices.nfc.mfkey32.api

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import kotlinx.coroutines.flow.Flow

interface MfKey32Api {
    fun hasNotification(): Flow<Boolean>
    suspend fun checkBruteforceFileExist(requestApi: FlipperRequestApi)
}