package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.painterResourceByKey
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.impl.model.FavoriteState

private const val ICON_SIZE_DP = 24

@Composable
fun ComposableFavorite(favoriteState: FavoriteState, onSwitchFavorites: (Boolean) -> Unit) {
    val iconId = when (favoriteState) {
        FavoriteState.PROGRESS -> {
            CircularProgressIndicator(modifier = Modifier.size(ICON_SIZE_DP.dp))
            return
        }
        FavoriteState.FAVORITE -> DesignSystem.drawable.ic_star_enabled
        FavoriteState.NOT_FAVORITE -> DesignSystem.drawable.ic_star_disabled
    }

    Icon(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = { // Inverse state
                    onSwitchFavorites(favoriteState != FavoriteState.FAVORITE)
                }
            )
            .size(ICON_SIZE_DP.dp),
        painter = painterResourceByKey(iconId),
        tint = LocalPallet.current.favorite,
        contentDescription = null
    )
}
