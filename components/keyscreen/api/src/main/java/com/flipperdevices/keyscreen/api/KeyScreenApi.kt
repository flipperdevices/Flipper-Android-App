package com.flipperdevices.keyscreen.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.model.KeyScreenState

interface KeyScreenApi {
    @Composable
    fun KeyCard(key: FlipperKeyParsed, deleted: Boolean, modifier: Modifier)

    @Composable
    fun KeyCard(
        onEdit: () -> Unit,
        onFavorite: (Boolean) -> Unit,
        state: KeyScreenState.Ready,
        modifier: Modifier
    )
}
