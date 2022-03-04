package com.flipperdevices.share.receive.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.deeplink.model.Deeplink

class KeyReceiveViewModelFactory(
    private val deeplink: Deeplink?,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KeyReceiveViewModel(deeplink, context) as T
    }
}
