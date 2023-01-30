package com.flipperdevices.keyedit.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface KeyEditTempStorage {
    fun getEditableKey(flipperKeyPath: FlipperKeyPath): EditableKey
    fun putEditableKey(flipperKeyPath: FlipperKeyPath, key: EditableKey)
}
