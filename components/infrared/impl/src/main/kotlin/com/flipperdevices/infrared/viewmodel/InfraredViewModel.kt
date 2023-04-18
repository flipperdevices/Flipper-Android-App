package com.flipperdevices.infrared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.infrared.api.EXTRA_KEY_PATH
import com.flipperdevices.infrared.models.InfraredKeyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class InfraredViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    private val keyPath: FlipperKeyPath,
    private val updaterKeyApi: UpdateKeyApi,
    private val simpleKeyApi: SimpleKeyApi,
    private val keyParser: KeyParser,
) : ViewModel(), LogTagProvider {
    override val TAG = "InfraredViewModel"

    private val state = MutableStateFlow<InfraredKeyState>(InfraredKeyState.Error)
    fun state() = state.asStateFlow()

    init {
        viewModelScope.launch { processFlowKey() }
    }

    private suspend fun processFlowKey() {
        if (keyPath.path.keyType != FlipperKeyType.INFRARED) {
            state.emit(InfraredKeyState.Error)
            warn { "Key type is not infrared" }
            return
        }

        viewModelScope.launch {
            updaterKeyApi.subscribeOnUpdatePath(keyPath).flatMapLatest {
                simpleKeyApi.getKeyAsFlow(it)
            }.collectLatest { flipperKey ->
                when {
                    flipperKey == null -> {
                        state.emit(InfraredKeyState.Error)
                    }
                    (flipperKey.flipperKeyType != FlipperKeyType.INFRARED) -> {
                        state.emit(InfraredKeyState.Error)
                    }
                    else -> {
                        processParserKey(flipperKey)
                    }
                }
            }
        }
    }

    private suspend fun processParserKey(flipperKey: FlipperKey) {
    }
}
