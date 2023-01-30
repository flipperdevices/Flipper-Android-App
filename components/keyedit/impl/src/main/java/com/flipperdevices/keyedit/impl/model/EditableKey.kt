package com.flipperdevices.keyedit.impl.model

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
sealed class EditableKey : Parcelable {
    @Parcelize
    @Serializable
    data class Existed(val flipperKeyPath: FlipperKeyPath) : EditableKey(), Parcelable

    @Parcelize
    @Serializable
    data class Limb(val notSavedFlipperKey: NotSavedFlipperKey) : EditableKey(), Parcelable
}
