package com.flipperdevices.share.impl.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.share.model.ShareFile

class ShareViewModelFactory(
    private val shareFile: ShareFile,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShareViewModel(shareFile, application) as T
    }
}
