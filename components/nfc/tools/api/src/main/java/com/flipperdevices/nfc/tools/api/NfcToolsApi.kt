package com.flipperdevices.nfc.tools.api

interface NfcToolsApi {
    suspend fun bruteforceKey(mfKey32Nonce: MfKey32Nonce): ULong?
}
