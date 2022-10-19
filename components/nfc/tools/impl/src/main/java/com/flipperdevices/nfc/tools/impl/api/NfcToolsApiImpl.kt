package com.flipperdevices.nfc.tools.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.nfc.tools.api.NfcToolsApi
import com.flipperdevices.nfc.tools.impl.bindings.MfKey32Binding
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, NfcToolsApi::class)
class NfcToolsApiImpl @Inject constructor() : NfcToolsApi, LogTagProvider {
    override val TAG = "NfcToolsApi"

    override fun test() {
        val key = MfKey32Binding.tryRecoverKey(
            uid = Integer.decode("2a234f80"),
            nt0 = Integer.decode("55721809"),
            nr0 = Integer.decode("ce9985f6"),
            ar0 = Integer.decode("772f55be"),
            nt1 = Integer.decode("a27173f2"),
            nr1 = Integer.decode("e386b505"),
            ar1 = Integer.decode("5fa65203")
        )
        info { "Read key $key" }
    }
}