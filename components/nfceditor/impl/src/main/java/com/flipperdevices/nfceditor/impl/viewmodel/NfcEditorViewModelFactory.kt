package com.flipperdevices.nfceditor.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.bridge.dao.api.model.FlipperKey

class NfcEditorViewModelFactory(
    private val flipperKey: FlipperKey
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NfcEditorViewModel(flipperKey) as T
    }
}
