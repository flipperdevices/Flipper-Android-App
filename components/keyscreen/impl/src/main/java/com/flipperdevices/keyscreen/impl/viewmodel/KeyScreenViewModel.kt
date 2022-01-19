package com.flipperdevices.keyscreen.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyScreenViewModel(
    private val keyPath: FlipperKeyPath?
) : ViewModel() {

    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var keyParser: KeyParser

    private val keyScreenState = MutableStateFlow<KeyScreenState>(KeyScreenState.InProgress)

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
        viewModelScope.launch {
            val keyPathNotNull = if (keyPath == null) {
                keyScreenState.update { KeyScreenState.Error(R.string.keyscreen_error_keypath) }
                return@launch
            } else keyPath
            val flipperKey = keyApi.getKey(keyPathNotNull)
            if (flipperKey == null) {
                keyScreenState.update {
                    KeyScreenState.Error(R.string.keyscreen_error_notfound_key)
                }
                return@launch
            } else {
                val parsedKey = keyParser.parseKey(flipperKey)
                keyScreenState.update { KeyScreenState.Ready(parsedKey) }
                return@launch
            }
        }
    }

    fun getKeyScreenState(): StateFlow<KeyScreenState> = keyScreenState
}
