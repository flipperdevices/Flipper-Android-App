package com.flipperdevices.keyedit.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

sealed class KeyEditState {
    object Loading : KeyEditState()
    data class Editing(
        val name: String?,
        val notes: String?,
        val parsedKey: FlipperKeyParsed,
        val savingKeyActive: Boolean
    ) : KeyEditState()

    data class Saving(
        val name: String?,
        val notes: String?,
        val parsedKey: FlipperKeyParsed
    ) : KeyEditState()

    data class Finished(
        val flipperKey: FlipperKey
    ) : KeyEditState()
}
