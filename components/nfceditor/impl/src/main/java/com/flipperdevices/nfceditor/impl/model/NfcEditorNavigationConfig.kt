package com.flipperdevices.nfceditor.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import kotlinx.serialization.Serializable

@Serializable
sealed class NfcEditorNavigationConfig {
    @Serializable
    data class NfcEditor(val flipperKeyPath: FlipperKeyPath) : NfcEditorNavigationConfig()

    @Serializable
    data class Save(val notSavedFlipperKey: NotSavedFlipperKey, val title: String?) : NfcEditorNavigationConfig()
}
