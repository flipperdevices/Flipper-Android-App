package com.flipperdevices.share.receive.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.deeplink.model.Deeplink

class KeyReceiveViewModelFactory(
    private val deeplink: Deeplink?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KeyReceiveViewModel(deeplink) as T
    }
}
