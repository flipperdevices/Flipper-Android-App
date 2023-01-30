package com.flipperdevices.keyedit.impl.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.EditableKey
import com.flipperdevices.keyedit.api.KeyEditTempStorage
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, KeyEditTempStorage::class)
class KeyEditTempStorageImpl @Inject constructor() : KeyEditTempStorage {
    private val editableKeysMap = mutableMapOf<FlipperKeyPath, EditableKey>()

    override fun getEditableKey(flipperKeyPath: FlipperKeyPath): EditableKey {
        val key = editableKeysMap[flipperKeyPath]
        editableKeysMap.remove(flipperKeyPath)
        return requireNotNull(key) { "Not exist $flipperKeyPath in storage" }
    }

    override fun putEditableKey(flipperKeyPath: FlipperKeyPath, key: EditableKey) {
        editableKeysMap[flipperKeyPath] = key
    }
}
