package com.flipperdevices.keyedit.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditApi
import com.flipperdevices.keyedit.impl.composable.ExternalEditScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyEditApiImpl @Inject constructor() : KeyEditApi {
    @Composable
    override fun EditScreen(
        flipperKey: FlipperKey,
        parsedKey: FlipperKeyParsed?,
        onKeyEditFinished: (FlipperKey) -> Unit
    ) {
        ExternalEditScreen(
            flipperKey = flipperKey,
            onKeyEditFinished = onKeyEditFinished,
            parsedKey = parsedKey
        )
    }
}
