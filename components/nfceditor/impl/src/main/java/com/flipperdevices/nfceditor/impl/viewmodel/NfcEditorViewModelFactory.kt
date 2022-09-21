package com.flipperdevices.nfceditor.impl.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.bridge.dao.api.model.FlipperKey

class NfcEditorViewModelFactory(
    private val application: Application,
    private val flipperKey: FlipperKey
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NfcEditorViewModel(application, flipperKey) as T
    }
}
