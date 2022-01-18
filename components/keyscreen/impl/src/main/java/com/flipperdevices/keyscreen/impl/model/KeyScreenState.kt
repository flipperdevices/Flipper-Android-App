package com.flipperdevices.keyscreen.impl.model

import androidx.annotation.StringRes
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

sealed class KeyScreenState {
    object Initial : KeyScreenState()
    class Error(@StringRes val reason: Int) : KeyScreenState()
    class Ready(val parsedKey: FlipperKeyParsed) : KeyScreenState()
}
