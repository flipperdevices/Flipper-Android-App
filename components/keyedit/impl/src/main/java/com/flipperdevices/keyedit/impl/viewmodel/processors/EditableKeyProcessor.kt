package com.flipperdevices.keyedit.impl.viewmodel.processors

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.flipperdevices.keyedit.impl.model.KeyEditState

interface EditableKeyProcessor<T : EditableKey> {
    suspend fun loadKey(editableKey: T, onStateUpdate: suspend (KeyEditState) -> Unit)
    suspend fun onSave(editableKey: T, editState: KeyEditState.Editing, onEndAction: (FlipperKey?) -> Unit)
}
