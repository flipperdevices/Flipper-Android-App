package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.archive.impl.model.ArchiveTab

class TabViewModelFactory(
    private val tab: ArchiveTab.Specified
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TabViewModel(tab) as T
    }
}
