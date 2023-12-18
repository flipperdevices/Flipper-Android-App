package com.flipperdevices.nfc.attack.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class NfcAttackViewModel @Inject constructor(
    private val mfKey32Api: MfKey32Api
) : ViewModel() {
    fun hasMfKey32Notification(): StateFlow<Boolean> = mfKey32Api.hasNotification()
        .stateIn(
            viewModelScope + Dispatchers.Default,
            SharingStarted.Eagerly,
            false
        )
}
