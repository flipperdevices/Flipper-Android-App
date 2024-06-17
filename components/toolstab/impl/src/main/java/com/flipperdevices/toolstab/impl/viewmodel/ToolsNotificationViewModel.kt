package com.flipperdevices.toolstab.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ToolsNotificationViewModel @Inject constructor(
    mfKey32Api: MfKey32Api
) : DecomposeViewModel() {
    val hasNotificationStateFlow: StateFlow<Boolean> = mfKey32Api
        .hasNotification()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}
