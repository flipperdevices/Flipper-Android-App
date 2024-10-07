package com.flipperdevices.infrared.editor.viewmodel

import android.content.Context
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
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
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.infrared.editor.R
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.infrared.editor.core.parser.InfraredKeyParser
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.charset.Charset

private const val MAX_SIZE_REMOTE_LENGTH = 21
private const val VIBRATOR_TIME_MS = 500L

@Suppress("TooManyFunctions")
class InfraredEditorViewModel @AssistedInject constructor(
    @Assisted private val keyPath: FlipperKeyPath,
    private val simpleKeyApi: SimpleKeyApi,
    private val updateKeyApi: UpdateKeyApi,
    private val synchronizationApi: SynchronizationApi,
    context: Context,
    private val settings: DataStore<Settings>
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "InfraredEditorViewModel"

    private val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)

    private val flipperKeyFlow = MutableStateFlow<FlipperKey?>(null)
    private val flipperParsedKeyFlow = MutableStateFlow<ImmutableList<InfraredRemote>?>(null)

    private val keyStateFlow = MutableStateFlow<InfraredEditorState>(InfraredEditorState.InProgress)
    fun getKeyState() = keyStateFlow.asStateFlow()

    private val dialogStateFlow = MutableStateFlow(false)
    fun getDialogState() = dialogStateFlow.asStateFlow()

    fun onDismissDialog() = viewModelScope.launch {
        dialogStateFlow.emit(false)
    }

    init {
        invalidate()
    }

    private fun invalidate() {
        viewModelScope.launch {
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

    fun processSave(
        currentState: InfraredEditorState.Ready,
        onExitScreen: () -> Unit
    ) = viewModelScope.launch {
        if (isDirtyKey(currentState).not()) {
            withContext(Dispatchers.Main) {
                onExitScreen()
            }
            return@launch
        }
        val errorRemotes = getErrorRemotes(currentState)
        info { "Errors remote: $errorRemotes count ${errorRemotes.size}" }

        if (errorRemotes.isNotEmpty()) {
            vibrator?.vibrateCompat(
                VIBRATOR_TIME_MS,
                settings.data.first().disabled_vibration
            )
            keyStateFlow.emit(
                InfraredEditorState.Ready(
                    remotes = currentState.remotes,
                    keyName = currentState.keyName,
                    errorRemotes = errorRemotes
                )
            )
            return@launch
        }
        val flipperKey = flipperKeyFlow.first() ?: return@launch
        val newFlipperKey = InfraredStateParser.mapStateToFlipperKey(flipperKey, currentState)

        updateKeyApi.updateKey(flipperKey, newFlipperKey)
        synchronizationApi.startSynchronization(force = true)

        withContext(Dispatchers.Main) {
            onExitScreen()
        }
    }

    private fun getErrorRemotes(state: InfraredEditorState.Ready): ImmutableList<Int> {
        val remotes = state.remotes
        val errorRemotes = mutableListOf<Int>()

        remotes.forEachIndexed { index, remote ->
            if (remote.name.isEmpty()) {
                errorRemotes.add(index)
            }
        }

        return errorRemotes.toPersistentList()
    }

    fun processCancel(
        currentState: InfraredEditorState,
        onExitScreen: () -> Unit
    ) = viewModelScope.launch {
        if (isDirtyKey(currentState)) {
            dialogStateFlow.emit(true)
        } else {
            withContext(Dispatchers.Main) {
                onExitScreen()
            }
        }
    }

    fun processDeleteRemote(
        currentState: InfraredEditorState.Ready,
        index: Int
    ) = viewModelScope.launch {
        val remotes = currentState.remotes.toMutableList()
        remotes.removeAt(index)

        keyStateFlow.emit(
            InfraredEditorState.Ready(
                remotes = remotes.toPersistentList(),
                keyName = currentState.keyName
            )
        )
    }

    private suspend fun isDirtyKey(currentState: InfraredEditorState): Boolean {
        if (currentState !is InfraredEditorState.Ready) return false
        val currentRemotes = currentState.remotes
        val initRemotes = flipperParsedKeyFlow.first() ?: return false

        return currentRemotes != initRemotes
    }

    fun processEditOrder(
        currentState: InfraredEditorState.Ready,
        from: Int,
        to: Int
    ) = viewModelScope.launch {
        val remotes = currentState.remotes.toMutableList()
        remotes.add(to, remotes.removeAt(from))

        keyStateFlow.emit(
            InfraredEditorState.Ready(
                remotes = remotes.toPersistentList(),
                keyName = currentState.keyName
            )
        )
    }

    fun editRemoteName(
        currentState: InfraredEditorState.Ready,
        index: Int,
        source: String
    ) {
        var value = FlipperSymbolFilter.filterUnacceptableSymbol(source)
        if (value.length > MAX_SIZE_REMOTE_LENGTH) {
            value = value.substring(0, MAX_SIZE_REMOTE_LENGTH)
            vibrator?.vibrateCompat(
                VIBRATOR_TIME_MS,
                runBlocking { settings.data.first().disabled_vibration }
            )
        }
        viewModelScope.launch {
            val remotes = currentState.remotes.toMutableList()
            remotes[index] = remotes[index].copy(name = value)

            keyStateFlow.emit(
                InfraredEditorState.Ready(
                    remotes = remotes.toPersistentList(),
                    keyName = currentState.keyName,
                    activeRemote = currentState.activeRemote
                )
            )
        }
    }

    fun processChangeIndexEditor(
        currentState: InfraredEditorState.Ready,
        index: Int
    ) = viewModelScope.launch {
        keyStateFlow.emit(
            InfraredEditorState.Ready(
                remotes = currentState.remotes,
                keyName = currentState.keyName,
                activeRemote = index
            )
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            keyPath: FlipperKeyPath
        ): InfraredEditorViewModel
    }
}
