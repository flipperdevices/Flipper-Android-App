package com.flipperdevices.nfc.attack.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

class NfcAttackViewModel @Inject constructor(
    private val mfKey32Api: MfKey32Api
) : DecomposeViewModel() {
    fun hasMfKey32Notification(): StateFlow<Boolean> = mfKey32Api.hasNotification()
        .stateIn(
            viewModelScope + Dispatchers.Default,
            SharingStarted.Eagerly,
            false
        )
}
