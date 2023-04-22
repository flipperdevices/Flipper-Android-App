package com.flipperdevices.keyscreen.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.keyscreen.api.state.DeleteState
import com.flipperdevices.keyscreen.impl.composable.card.ComposableKeyCard
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyScreenApiImpl @Inject constructor() : KeyScreenApi {
    @Composable
    override fun KeyCard(key: FlipperKeyParsed, deleted: Boolean, modifier: Modifier) {
        ComposableKeyCard(
            modifier = modifier,
            parsedKey = key,
            synchronizationState = null,
            deleteState = if (deleted) DeleteState.DELETED else DeleteState.NOT_DELETED
        )
    }
}
