package com.flipperdevices.keyscreen.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

@Immutable
sealed class KeyScreenState {
    data object InProgress : KeyScreenState()
    class Error(@StringRes val reason: Int) : KeyScreenState()
    data class Ready(
        val parsedKey: FlipperKeyParsed,
        val favoriteState: FavoriteState,
        val shareState: ShareState,
        val deleteState: DeleteState,
        val flipperKey: FlipperKey,
        val emulateConfig: EmulateConfig?,
        val isSupportEditing: Boolean,
        val emulatingInProgress: Boolean
    ) : KeyScreenState()
}
