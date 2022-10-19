package com.flipperdevices.nfc.tools.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.nfc.tools.api.NfcToolsApi
import com.flipperdevices.nfc.tools.impl.bindings.MfKey32Binding
import com.squareup.anvil.annotations.ContributesBinding
import java.lang.Long.parseLong
import javax.inject.Inject

@ContributesBinding(AppGraph::class, NfcToolsApi::class)
class NfcToolsApiImpl @Inject constructor() : NfcToolsApi, LogTagProvider {
    override val TAG = "NfcToolsApi"

    @Suppress("MagicNumber")
    override fun test() {
        val key = MfKey32Binding.tryRecoverKey(
            uid = parseLong("2a234f80", 16),
            nt0 = parseLong("55721809", 16),
            nr0 = parseLong("ce9985f6", 16),
            ar0 = parseLong("772f55be", 16),
            nt1 = parseLong("a27173f2", 16),
            nr1 = parseLong("e386b505", 16),
            ar1 = parseLong("5fa65203", 16)
        )
        info { "Read key $key" }
    }
}
