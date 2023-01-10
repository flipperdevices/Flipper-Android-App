package com.flipperdevices.keyedit.impl.viewmodel.processors

import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.keyedit.impl.R
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.flipperdevices.keyedit.impl.model.KeyEditState
import com.flipperdevices.singleactivity.api.SingleActivityApi
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

private const val NOTIFICATION_DURATION_MS = 3 * 1000L

class LimboKeyProcessor @Inject constructor(
    private val parser: KeyParser,
    private val utilsKeyApi: UtilsKeyApi,
    private val simpleKeyApi: SimpleKeyApi,
    private val synchronizationApi: SynchronizationApi,
    private val inAppNotificationStorage: InAppNotificationStorage,
    private val singleActivityApi: SingleActivityApi
) : EditableKeyProcessor<EditableKey.Limb> {
    override suspend fun loadKey(
        editableKey: EditableKey.Limb,
        onStateUpdate: suspend (KeyEditState) -> Unit
    ) {
        val newPath = utilsKeyApi.findAvailablePath(
            FlipperKeyPath(
                editableKey.notSavedFlipperKey.mainFile.path,
                deleted = false
            )
        ).path

        val parsedKeyLoaded = parser.parseKey(editableKey.notSavedFlipperKey.toFlipperKey(newPath))
        onStateUpdate(
            KeyEditState.Editing(
                newPath.nameWithoutExtension,
                editableKey.notSavedFlipperKey.notes,
                parsedKeyLoaded,
                newPath.nameWithoutExtension.isNotBlank()
            )
        )
    }

    override suspend fun onSave(
        editableKey: EditableKey.Limb,
        editState: KeyEditState.Editing,
        router: Router
    ) {
        val newPathUnfree = if (editState.name != null) {
            editableKey.notSavedFlipperKey.mainFile.path.copyWithChangedName(editState.name)
        } else {
            editableKey.notSavedFlipperKey.mainFile.path
        }
        val newPath = utilsKeyApi.findAvailablePath(
            FlipperKeyPath(newPathUnfree, deleted = false)
        ).path

        val newKey = editableKey.notSavedFlipperKey.toFlipperKey(newPath).copy(
            notes = editState.notes
        )

        simpleKeyApi.insertKey(newKey)
        synchronizationApi.startSynchronization(force = true)
        inAppNotificationStorage.addNotification(
            InAppNotification(
                title = newKey.path.nameWithExtension,
                descriptionId = R.string.keyedit_notification_desc,
                durationMs = NOTIFICATION_DURATION_MS
            )
        )
        singleActivityApi.open()
    }
}

private fun NotSavedFlipperKey.toFlipperKey(newPath: FlipperFilePath) = FlipperKey(
    mainFile = FlipperFile(newPath, mainFile.content),
    additionalFiles = additionalFiles.map {
        FlipperFile(
            it.path.copyWithChangedName(newPath.nameWithoutExtension),
            it.content
        )
    },
    notes = notes,
    deleted = false,
    synchronized = false
)
