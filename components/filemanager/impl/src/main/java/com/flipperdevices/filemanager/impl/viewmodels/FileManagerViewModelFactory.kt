package com.flipperdevices.filemanager.impl.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FileManagerViewModelFactory(
    private val path: String
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FileManagerViewModel(path) as T
    }
}
