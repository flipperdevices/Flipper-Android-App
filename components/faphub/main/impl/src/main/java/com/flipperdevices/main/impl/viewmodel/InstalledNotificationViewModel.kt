package com.flipperdevices.main.impl.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InstalledNotificationViewModel : ViewModel() {
    private val notificationCountStateFlow = MutableStateFlow(value = 4)

    fun getNotificationCountStateFlow(): StateFlow<Int> = notificationCountStateFlow
}
