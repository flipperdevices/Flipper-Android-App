package com.flipperdevices.keyscreen.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.keyscreen.impl.composable.card.ComposableKeyCard
import com.flipperdevices.keyscreen.model.DeleteState
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyScreenApiImpl @Inject constructor(
    private val synchronizationUiApi: SynchronizationUiApi
) : KeyScreenApi {
    @Composable
    override fun KeyCard(key: FlipperKeyParsed, deleted: Boolean, modifier: Modifier) {
        ComposableKeyCard(
            modifier = modifier,
            parsedKey = key,
            synchronizationState = null,
            deleteState = if (deleted) DeleteState.DELETED else DeleteState.NOT_DELETED
        )
    }

    @Composable
    override fun KeyCard(
        onEdit: () -> Unit,
        onFavorite: (Boolean) -> Unit,
        state: KeyScreenState.Ready,
        modifier: Modifier
    ) {
        ComposableKeyCard(
            parsedKey = state.parsedKey,
            deleteState = state.deleteState,
            modifier = modifier,
            synchronizationState = if (state.deleteState == DeleteState.NOT_DELETED) {
                {
                    synchronizationUiApi.RenderSynchronizationState(
                        state.flipperKey.getKeyPath(),
                        withText = true
                    )
                }
            } else {
                null
            },
            favoriteState = state.favoriteState,
            onSwitchFavorites = onFavorite,
            onEditName = onEdit
        )
    }
}
