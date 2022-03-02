package com.flipperdevices.keyedit.impl.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

class KeyEditViewModelFactory(
    private val flipperKey: FlipperKey,
    private val context: Context,
    private val parsedKey: FlipperKeyParsed? = null
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KeyEditViewModel(flipperKey, context, parsedKey) as T
    }
}
