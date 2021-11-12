package com.flipperdevices.share.receive.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReceiveViewModelFactory(
    private val receiveFileUri: Uri,
    private val flipperPath: String,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReceiveViewModel(receiveFileUri, flipperPath, application) as T
    }
}
