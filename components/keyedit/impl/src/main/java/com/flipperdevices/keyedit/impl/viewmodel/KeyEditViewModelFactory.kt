package com.flipperdevices.keyedit.impl.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

class KeyEditViewModelFactory(
    private val keyPath: FlipperKeyPath,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KeyEditViewModel(keyPath, application) as T
    }
}
