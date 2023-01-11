package com.flipperdevices.keyedit.impl.model

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class EditableKey : Parcelable {
    @Parcelize
    data class Existed(val flipperKeyPath: FlipperKeyPath) : EditableKey(), Parcelable

    @Parcelize
    data class Limb(val notSavedFlipperKey: NotSavedFlipperKey) : EditableKey(), Parcelable
}
