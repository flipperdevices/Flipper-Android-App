package com.flipperdevices.keyedit.impl.model

import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

sealed class KeyEditState {
    object Loading : KeyEditState()
    data class Editing(
        val name: String?,
        val notes: String?,
        val parsedKey: FlipperKeyParsed,
        val savingKeyActive: Boolean
    ) : KeyEditState()

    data class Saving(
        val editState: Editing
    ) : KeyEditState()

    object Failed : KeyEditState()
}
