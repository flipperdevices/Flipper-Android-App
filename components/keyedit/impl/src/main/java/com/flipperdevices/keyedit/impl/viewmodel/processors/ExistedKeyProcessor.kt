package com.flipperdevices.keyedit.impl.viewmodel.processors

import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.flipperdevices.keyedit.impl.model.KeyEditState
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class ExistedKeyProcessor @Inject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val updateKeyApi: UpdateKeyApi,
    private val synchronizationApi: SynchronizationApi,
    private val parser: KeyParser
) : EditableKeyProcessor<EditableKey.Existed> {
    override suspend fun loadKey(
        editableKey: EditableKey.Existed,
        onStateUpdate: suspend (KeyEditState) -> Unit
    ) {
        val flipperKey = simpleKeyApi.getKey(editableKey.flipperKeyPath)
        if (flipperKey == null) {
            onStateUpdate(KeyEditState.Failed)
            return
        }
        val parsedKeyLoaded = parser.parseKey(flipperKey)
        onStateUpdate(
            KeyEditState.Editing(
                flipperKey.path.nameWithoutExtension,
                flipperKey.notes,
                parsedKeyLoaded,
                flipperKey.path.nameWithoutExtension.isNotBlank()
            )
        )
    }

    override suspend fun onSave(
        editableKey: EditableKey.Existed,
        editState: KeyEditState.Editing,
        router: Router
    ) {
        try {
            val oldKey = simpleKeyApi.getKey(editableKey.flipperKeyPath) ?: return
            val extension =
                editableKey.flipperKeyPath.path.nameWithExtension.substringAfterLast('.')
            val newFlipperKey = oldKey.copy(
                mainFile = oldKey.mainFile.copy(
                    path = FlipperFilePath(
                        editableKey.flipperKeyPath.path.folder,
                        "${editState.name}.$extension"
                    )
                ),
                notes = editState.notes
            )
            updateKeyApi.updateKey(oldKey, newFlipperKey)
            synchronizationApi.startSynchronization(force = true)
        } finally {
            router.exit()
        }
    }
}
