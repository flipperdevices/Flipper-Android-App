package com.flipperdevices.keyedit.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import kotlinx.serialization.Serializable

@Serializable
sealed class EditableKey {
    @Serializable
    data class Existed(val flipperKeyPath: FlipperKeyPath) : EditableKey()

    @Serializable
    data class Limb(val notSavedFlipperKey: NotSavedFlipperKey) : EditableKey()
}
