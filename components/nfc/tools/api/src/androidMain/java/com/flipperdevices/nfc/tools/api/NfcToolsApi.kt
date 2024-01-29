package com.flipperdevices.nfc.tools.api

import java.math.BigInteger

interface NfcToolsApi {
    suspend fun bruteforceKey(mfKey32Nonce: MfKey32Nonce): BigInteger?
}
