package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableEdit
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableShare
import com.flipperdevices.keyscreen.impl.composable.card.ComposableKeyCard
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitle
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar

@Composable
fun ComposableKeyParsed(
    viewModel: KeyScreenViewModel,
    keyScreenState: KeyScreenState.Ready,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ComposableKeyScreenBar(onBack)
        ComposableKeyCard(
            keyScreenState.parsedKey,
            keyScreenState.favoriteState,
            viewModel::setFavorite
        )
        ComposableEdit(viewModel::onOpenEdit)
        ComposableShare(keyScreenState.shareState, viewModel::onShare)
    }
}

@Composable
private fun ComposableKeyScreenBar(onBack: () -> Unit) {
    ComposableKeyScreenAppBar(
        centerBlock = {
            ComposableBarTitle(modifier = it, textId = R.string.keyscreen_title)
        },
        endBlock = {
            Icon(
                modifier = it
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        onClick = onBack
                    ),
                painter = painterResource(R.drawable.ic_close_icon),
                contentDescription = stringResource(R.string.keyscreen_cancel_pic_desc),
                tint = colorResource(DesignSystem.color.black_100)
            )
        }
    )
}
