package com.flipperdevices.nfc.mfkey32.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.nfc.tools.api.NfcToolsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class MfKey32ViewModel @VMInject constructor(
    private val nfcToolsApi: NfcToolsApi
) : ViewModel(), LogTagProvider {
    override val TAG = "MfKey32ViewModel"

    fun runNfcTool() {
        viewModelScope.launch(Dispatchers.Default) {
            info { "Start nfc test" }
            nfcToolsApi.test()
            info { "Finish nfc test" }
        }
    }
}