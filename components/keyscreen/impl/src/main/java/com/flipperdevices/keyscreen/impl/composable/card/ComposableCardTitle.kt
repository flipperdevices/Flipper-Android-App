package com.flipperdevices.keyscreen.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableFavorite
import com.flipperdevices.keyscreen.impl.model.DeleteState
import com.flipperdevices.keyscreen.impl.model.FavoriteState

@Composable
fun ComposableCardTitle(
    modifier: Modifier,
    keyName: String,
    deleteState: DeleteState,
    favoriteState: FavoriteState? = null,
    onSwitchFavorites: ((Boolean) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = keyName,
            fontWeight = FontWeight.W500,
            fontSize = 18.sp
        )
        if (deleteState == DeleteState.NOT_DELETED &&
            favoriteState != null &&
            onSwitchFavorites != null
        ) {
            ComposableFavorite(favoriteState, onSwitchFavorites)
        }
    }
}
