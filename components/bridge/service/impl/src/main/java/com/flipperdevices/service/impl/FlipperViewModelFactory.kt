package com.flipperdevices.service.impl

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FlipperViewModelFactory(
    private val application: Application,
    private val deviceId: String
) : ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FlipperViewModel(application, deviceId) as T
    }
}
