package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel

@Composable
fun ComposableKeyAction(viewModel: KeyScreenViewModel, keyScreenState: KeyScreenState.Ready) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    viewModel.setFavorite(!keyScreenState.isFavorite)
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (keyScreenState.isFavorite) {
                ComposableFavoriteEnabled()
            } else ComposableFavoriteDisabled()
        }
    }
}

@Composable
private fun RowScope.ComposableFavoriteDisabled() {
    Icon(
        modifier = Modifier
            .size(size = 32.dp),
        painter = painterResource(R.drawable.ic_star_disabled),
        contentDescription = stringResource(
            R.string.keyscreen_favorite_disabled
        )
    )

    Text(
        modifier = Modifier.padding(start = 10.dp),
        text = stringResource(R.string.keyscreen_favorite_disabled),
        fontWeight = FontWeight.W500,
        fontSize = 16.sp
    )
}

@Composable
private fun RowScope.ComposableFavoriteEnabled() {
    Icon(
        modifier = Modifier
            .size(size = 32.dp),
        painter = painterResource(R.drawable.ic_star_enabled),
        contentDescription = stringResource(
            R.string.keyscreen_favorite_enabled
        ),
        tint = colorResource(R.color.keyscreen_favorite_enabled)
    )

    Text(
        modifier = Modifier.padding(start = 10.dp),
        text = stringResource(R.string.keyscreen_favorite_enabled),
        fontWeight = FontWeight.W500,
        fontSize = 16.sp
    )
}
