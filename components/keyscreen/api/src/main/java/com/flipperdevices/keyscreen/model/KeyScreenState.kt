package com.flipperdevices.keyscreen.model

import androidx.annotation.StringRes
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

sealed class KeyScreenState {
    object InProgress : KeyScreenState()
    class Error(@StringRes val reason: Int) : KeyScreenState()
    data class Ready constructor(
        val parsedKey: FlipperKeyParsed,
        val favoriteState: FavoriteState,
        val shareState: ShareState,
        val deleteState: DeleteState,
        val flipperKey: FlipperKey
    ) : KeyScreenState()
}
