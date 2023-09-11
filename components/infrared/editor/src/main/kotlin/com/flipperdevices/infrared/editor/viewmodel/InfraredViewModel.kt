package com.flipperdevices.infrared.editor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.infrared.editor.R
import com.flipperdevices.infrared.editor.api.EXTRA_KEY_PATH
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject
import java.nio.charset.Charset

class InfraredViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    private val keyPath: FlipperKeyPath,
    private val simpleKeyApi: SimpleKeyApi
) : ViewModel() {
    private val keyStateFlow = MutableStateFlow<InfraredEditorState>(InfraredEditorState.InProgress)
    fun getKeyState() = keyStateFlow.asStateFlow()

    private val dialogStateFlow = MutableStateFlow(false)
    fun getDialogState() = dialogStateFlow.asStateFlow()

    fun onDismissDialog() = viewModelScope.launch {
        dialogStateFlow.emit(false)
    }

    init { invalidate() }

    private fun invalidate() {
        viewModelScope.launch(Dispatchers.Default) {
            val flipperKey = simpleKeyApi.getKey(keyPath)
            if (flipperKey == null) {
                keyStateFlow.emit(InfraredEditorState.Error(R.string.infrared_editor_not_found_key))
                return@launch
            }

            val fileContent = flipperKey.keyContent.openStream().use {
                it.readBytes().toString(Charset.defaultCharset())
            }
            val fff = FlipperFileFormat.fromFileContent(fileContent)
            val infraredParsed = InfraredEditorParser
                .mapParsedKeyToInfraredRemotes(fff)
                .toPersistentList()
            keyStateFlow.emit(
                InfraredEditorState.Ready(
                    keyName = flipperKey.path.nameWithoutExtension,
                    remotes = infraredParsed
                )
            )
        }
    }

    fun processSave() {
        TODO("Not yet implemented")
    }

    fun processCancel() {
        TODO("Not yet implemented")
    }

    fun processDeleteRemote(index: Int) {
    }
}
