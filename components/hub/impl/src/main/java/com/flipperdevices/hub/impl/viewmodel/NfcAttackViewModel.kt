package com.flipperdevices.hub.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.nfc.attack.api.NfcAttackApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class NfcAttackViewModel @Inject constructor(
    nfcAttackApi: NfcAttackApi
) : ViewModel() {
    private val nfcAttackNotificationCountStateFlow = MutableStateFlow(0)

    init {
        nfcAttackApi.notificationCount().onEach {
            nfcAttackNotificationCountStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    fun getNfcAttackNotificationCountState(): StateFlow<Int> = nfcAttackNotificationCountStateFlow
}
