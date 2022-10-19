package com.flipperdevices.nfc.tools.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.nfc.tools.api.MfKey32Nonce
import com.flipperdevices.nfc.tools.api.NfcToolsApi
import com.flipperdevices.nfc.tools.impl.bindings.MfKey32Binding
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, NfcToolsApi::class)
class NfcToolsApiImpl @Inject constructor() : NfcToolsApi, LogTagProvider {
    override val TAG = "NfcToolsApi"


    override suspend fun bruteforceKey(mfKey32Nonce: MfKey32Nonce): ULong? {
        val key = MfKey32Binding.tryRecoverKey(
            uid = mfKey32Nonce.uid.toLong(),
            nt0 = mfKey32Nonce.nt0.toLong(),
            nr0 = mfKey32Nonce.nr0.toLong(),
            ar0 = mfKey32Nonce.ar0.toLong(),
            nt1 = mfKey32Nonce.nt1.toLong(),
            nr1 = mfKey32Nonce.nr1.toLong(),
            ar1 = mfKey32Nonce.ar1.toLong()
        )
        return key?.toULongOrNull()
    }
}
