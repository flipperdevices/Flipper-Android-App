package com.flipperdevices.infrared.editor.viewmodel

import android.content.Context
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.utils.FlipperSymbolFilter
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.infrared.editor.R
import com.flipperdevices.infrared.editor.api.EXTRA_KEY_PATH
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import com.flipperdevices.infrared.editor.model.InfraredRemote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject
import java.nio.charset.Charset

private const val MAX_SIZE_REMOTE_LENGTH = 21
private const val VIBRATOR_TIME_MS = 500L

@Suppress("TooManyFunctions")
class InfraredViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    private val keyPath: FlipperKeyPath,
    private val simpleKeyApi: SimpleKeyApi,
    private val updateKeyApi: UpdateKeyApi,
    private val synchronizationApi: SynchronizationApi,
    context: Context
) : ViewModel(), LogTagProvider {
    override val TAG: String = "InfraredViewModel"

    private var vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)

    private val flipperKeyFlow = MutableStateFlow<FlipperKey?>(null)
    private val flipperParsedKeyFlow = MutableStateFlow<ImmutableList<InfraredRemote>?>(null)

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
            val infraredParsed = InfraredKeyParser
                .mapParsedKeyToInfraredRemotes(fff)
                .toPersistentList()

            flipperKeyFlow.emit(flipperKey)
            flipperParsedKeyFlow.emit(infraredParsed)

            keyStateFlow.emit(
                InfraredEditorState.Ready(
                    keyName = flipperKey.path.nameWithoutExtension,
                    remotes = infraredParsed
                )
            )
        }
    }

    fun processSave(onEndAction: () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            if (isDirtyKey().not()) {
                withContext(Dispatchers.Main) {
                    onEndAction()
                    return@withContext
                }
            }
            val keyState = keyStateFlow.first()
            val remotes = keyState as? InfraredEditorState.Ready ?: return@launch
            val errorRemotes = getErrorRemotes(remotes)
            info { "Errors remote: $errorRemotes count ${errorRemotes.size}" }

            if (errorRemotes.isNotEmpty()) {
                vibrator?.vibrateCompat(VIBRATOR_TIME_MS)
                keyStateFlow.emit(
                    InfraredEditorState.Ready(
                        remotes = keyState.remotes,
                        keyName = keyState.keyName,
                        errorRemotes = errorRemotes
                    )
                )
                return@launch
            }
            val flipperKey = flipperKeyFlow.first() ?: return@launch
            val newFlipperKey = InfraredStateParser.mapStateToFlipperKey(flipperKey, remotes)

            updateKeyApi.updateKey(flipperKey, newFlipperKey)
            synchronizationApi.startSynchronization(force = true)

            withContext(Dispatchers.Main) {
                onEndAction()
            }
        }
    }

    private fun getErrorRemotes(state: InfraredEditorState.Ready): ImmutableList<Int> {
        val remotes = state.remotes
        val errorRemotes = mutableListOf<Int>()

        remotes.forEachIndexed { index, remote ->
            val countRepeat = remotes.count { it.name == remote.name }
            if (remote.name.isEmpty() || countRepeat > 1) {
                errorRemotes.add(index)
            }
        }

        return errorRemotes.toPersistentList()
    }

    fun processCancel(onEndAction: () -> Unit) {
        viewModelScope.launch {
            if (isDirtyKey()) {
                dialogStateFlow.emit(true)
            } else {
                withContext(Dispatchers.Main) {
                    onEndAction()
                }
            }
        }
    }

    fun processDeleteRemote(index: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val keyState = keyStateFlow.first() as? InfraredEditorState.Ready ?: return@launch
            val remotes = keyState.remotes.toMutableList()
            remotes.removeAt(index)

            keyStateFlow.emit(
                InfraredEditorState.Ready(
                    remotes = remotes.toPersistentList(),
                    keyName = keyState.keyName
                )
            )
        }
    }

    private suspend fun isDirtyKey(): Boolean {
        val keyState = keyStateFlow.first()
        if (keyState !is InfraredEditorState.Ready) return false
        val currentRemotes = keyState.remotes
        val initRemotes = flipperParsedKeyFlow.first() ?: return false

        return currentRemotes != initRemotes
    }

    fun processEditOrder(from: Int, to: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val keyState = keyStateFlow.first()
            if (keyState !is InfraredEditorState.Ready) return@launch

            val remotes = keyState.remotes.toMutableList()
            remotes.add(to, remotes.removeAt(from))

            keyStateFlow.emit(
                InfraredEditorState.Ready(
                    remotes = remotes.toPersistentList(),
                    keyName = keyState.keyName,
                    activeRemote = null
                )
            )
        }
    }

    fun editRemoteName(index: Int, source: String) {
        var value = FlipperSymbolFilter.filterUnacceptableSymbol(source)
        if (value.length > MAX_SIZE_REMOTE_LENGTH) {
            value = value.substring(0, MAX_SIZE_REMOTE_LENGTH)
            vibrator?.vibrateCompat(VIBRATOR_TIME_MS)
        }
        viewModelScope.launch(Dispatchers.Default) {
            val keyState = keyStateFlow.first()
            if (keyState !is InfraredEditorState.Ready) return@launch

            val remotes = keyState.remotes.toMutableList()
            remotes[index] = remotes[index].copy(name = value)

            keyStateFlow.emit(
                InfraredEditorState.Ready(
                    remotes = remotes.toPersistentList(),
                    keyName = keyState.keyName,
                    activeRemote = keyState.activeRemote
                )
            )
        }
    }

    fun processChangeIndexEditor(index: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val keyState = keyStateFlow.first()
            if (keyState !is InfraredEditorState.Ready) return@launch

            keyStateFlow.emit(
                InfraredEditorState.Ready(
                    remotes = keyState.remotes,
                    keyName = keyState.keyName,
                    activeRemote = index
                )
            )
        }
    }
}
