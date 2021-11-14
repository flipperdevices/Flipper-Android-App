package com.flipperdevices.share.receive.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.deeplink.model.DeeplinkContent

class ReceiveViewModelFactory(
    private val deeplinkContent: DeeplinkContent,
    private val flipperPath: String,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReceiveViewModel(deeplinkContent, flipperPath, application) as T
    }
}
