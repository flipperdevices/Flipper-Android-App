package com.flipperdevices.share.receive.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.deeplink.model.Deeplink

class KeyReceiveViewModelFactory(
    private val deeplink: Deeplink?,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KeyReceiveViewModel(deeplink, application) as T
    }
}
