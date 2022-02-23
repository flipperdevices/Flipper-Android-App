package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.FavoriteState

@Composable
fun ComposableFavorite(favoriteState: FavoriteState, onSwitchFavorites: (Boolean) -> Unit) {
    val textId = when (favoriteState) {
        FavoriteState.FAVORITE -> R.string.keyscreen_favorite_enabled
        FavoriteState.NOT_FAVORITE -> R.string.keyscreen_favorite_disabled
        FavoriteState.PROGRESS -> R.string.keyscreen_favorite_progress
    }
    val iconId = when (favoriteState) {
        FavoriteState.PROGRESS -> {
            ComposableActionRowInProgress(
                descriptionId = textId,
                descriptionColorId = DesignSystem.color.black_40
            )
            return
        }
        FavoriteState.FAVORITE -> R.drawable.ic_star_enabled
        FavoriteState.NOT_FAVORITE -> R.drawable.ic_star_disabled
    }
    val tintId = if (favoriteState == FavoriteState.FAVORITE) {
        R.color.keyscreen_favorite_enabled
    } else DesignSystem.color.black_100

    ComposableActionRow(
        iconId = iconId,
        tintId = tintId,
        descriptionId = textId,
        descriptionColorId = DesignSystem.color.black_100,
        onClick = { // Inverse state
            onSwitchFavorites(favoriteState != FavoriteState.FAVORITE)
        }
    )
}
