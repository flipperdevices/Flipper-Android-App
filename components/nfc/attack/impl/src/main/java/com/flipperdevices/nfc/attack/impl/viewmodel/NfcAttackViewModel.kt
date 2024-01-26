package com.flipperdevices.nfc.attack.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class NfcAttackViewModel @Inject constructor(
    private val mfKey32Api: MfKey32Api
) : DecomposeViewModel() {
    private val hasMfKey32NotificationFlow = MutableStateFlow<Boolean>(false)

    init {
        mfKey32Api.hasNotification()
            .onEach {
                hasMfKey32NotificationFlow.emit(it)
            }.launchIn(viewModelScope)
    }

    fun hasMfKey32Notification() = hasMfKey32NotificationFlow.asStateFlow()
}
