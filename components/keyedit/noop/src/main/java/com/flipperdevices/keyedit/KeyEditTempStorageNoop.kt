package com.flipperdevices.keyedit

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.EditableKey
import com.flipperdevices.keyedit.api.KeyEditTempStorage
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, KeyEditTempStorage::class)
class KeyEditTempStorageNoop @Inject constructor() : KeyEditTempStorage {
    override fun getEditableKey(flipperKeyPath: FlipperKeyPath): EditableKey {
        return EditableKey.Existed(flipperKeyPath = FlipperKeyPath(
            path = FlipperFilePath(
                folder = "any",
                nameWithExtension = "test.nfc"
            ),
            deleted = false
        ))
    }

    override fun putEditableKey(flipperKeyPath: FlipperKeyPath, key: EditableKey) = Unit
}
