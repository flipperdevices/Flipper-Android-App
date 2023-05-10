package com.flipperdevices.infrared.editor.model

import com.flipperdevices.bridge.dao.api.model.infrared.InfraredRemote
import kotlinx.collections.immutable.ImmutableList

sealed class InfraredEditorState {
    object Loading : InfraredEditorState()
    object Error : InfraredEditorState()

    data class LoadedKey(
        val name: String,
        val remotes: ImmutableList<InfraredRemote>
    ) : InfraredEditorState()
}
