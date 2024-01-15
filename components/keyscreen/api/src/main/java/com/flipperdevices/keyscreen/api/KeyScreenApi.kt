package com.flipperdevices.keyscreen.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.model.KeyScreenState

@Immutable
interface KeyScreenApi {
    @Composable
    fun KeyCard(key: FlipperKeyParsed, deleted: Boolean, modifier: Modifier)

    @Composable
    fun KeyCard(
        componentContext: ComponentContext,
        onEdit: () -> Unit,
        onFavorite: (Boolean) -> Unit,
        state: KeyScreenState.Ready,
        modifier: Modifier
    )
}
