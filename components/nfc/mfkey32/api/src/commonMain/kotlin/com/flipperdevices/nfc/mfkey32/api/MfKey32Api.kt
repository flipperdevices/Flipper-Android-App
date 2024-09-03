package com.flipperdevices.nfc.mfkey32.api

import kotlinx.coroutines.flow.Flow

interface MfKey32Api {
    /**
     * Cached value of bruteforce file existing
     *
     * This should be updated each [checkBruteforceFileExist]
     */
    val isBruteforceFileExist: Boolean

    fun hasNotification(): Flow<Boolean>
    suspend fun checkBruteforceFileExist()
}
