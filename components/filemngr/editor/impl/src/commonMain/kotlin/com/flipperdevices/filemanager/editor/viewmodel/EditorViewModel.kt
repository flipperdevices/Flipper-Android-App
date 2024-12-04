package com.flipperdevices.filemanager.editor.viewmodel

import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.limit
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.editor.model.EditorEncodingEnum
import com.flipperdevices.filemanager.editor.model.HexString
import com.flipperdevices.filemanager.editor.util.HexConverter
import com.flipperdevices.filemanager.util.constant.FileManagerConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Buffer
import okio.Path
import okio.buffer
import okio.use

class EditorViewModel @AssistedInject constructor(
    @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
    @Assisted("fullPathOnDevice") private val fullPathOnDevice: Path,
    private val storageProvider: FlipperStorageProvider
) : DecomposeViewModel() {

    private val _state = MutableStateFlow(
        State(
            fullPathOnFlipper = fullPathOnFlipper,
            hexString = HexString.Text("")
        )
    )
    val state = _state.asStateFlow()

    fun onFlipperPathChanged(fullPathOnFlipper: Path) {
        _state.update { state -> state.copy(fullPathOnFlipper = fullPathOnFlipper) }
    }

    fun getRawContent(): ByteArray? {
        val hexString = state.value.hexString
        return kotlin.runCatching { HexConverter.fromHexString(hexString).content.toByteArray() }
            .onFailure { error(it) { "#onEditorTypeChange could not transform hex" } }
            .getOrNull()
    }

    fun onTextChanged(text: String) {
        _state.update { state ->
            when (state.hexString) {
                is HexString.Hex -> state.copy(hexString = HexString.Hex(text))
                is HexString.Text -> state.copy(hexString = HexString.Text(text))
            }
        }
    }

    fun onEditorTypeChange(type: EditorEncodingEnum) {
        _state.update { state -> state.copy(canEdit = false) }
        _state.update { state ->
            if (state.encoding == type) return@update state
            // We can't always transform hex into string
            // As example, user can write letters beyond 0..F
            val hexStringResult = kotlin.runCatching {
                when (type) {
                    EditorEncodingEnum.TEXT -> {
                        HexConverter.fromHexString(state.hexString)
                    }

                    EditorEncodingEnum.HEX -> {
                        HexConverter.toHexString(state.hexString)
                    }
                }
            }.onFailure { error(it) { "#onEditorTypeChange could not transform hex" } }

            state.copy(
                hexString = hexStringResult.getOrElse { state.hexString },
                canEdit = true
            )
        }
    }

    fun writeNow() {
        val byteArray = getRawContent() ?: return
        val buffer = Buffer()
        buffer.write(byteArray)
        storageProvider
            .fileSystem
            .sink(fullPathOnDevice)
            .write(buffer, byteArray.size.toLong())
    }

    private fun loadText() {
        val content = storageProvider
            .fileSystem
            .source(fullPathOnDevice)
            .limit(FileManagerConstants.LIMITED_SIZE_BYTES)
            .buffer()
            .use { bufferedSource -> bufferedSource.readUtf8() }
        _state.update { state -> state.copy(hexString = HexString.Text(content)) }
    }

    init {
        viewModelScope.launch { loadText() }
    }

    data class State(
        val fullPathOnFlipper: Path,
        val hexString: HexString,
        val canEdit: Boolean = true
    ) {
        val encoding: EditorEncodingEnum = when (hexString) {
            is HexString.Hex -> EditorEncodingEnum.HEX
            is HexString.Text -> EditorEncodingEnum.TEXT
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
            @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
        ): EditorViewModel
    }
}
