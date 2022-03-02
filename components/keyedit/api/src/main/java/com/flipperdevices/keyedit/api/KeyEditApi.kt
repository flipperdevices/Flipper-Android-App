package com.flipperdevices.keyedit.api

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

interface KeyEditApi {
    /**
     * @param parsedKey can be null. In this case edit screen parse key itself
     */
    @Composable
    fun EditScreen(
        flipperKey: FlipperKey,
        parsedKey: FlipperKeyParsed?,
        onKeyEditFinished: (FlipperKey) -> Unit
    )
}
