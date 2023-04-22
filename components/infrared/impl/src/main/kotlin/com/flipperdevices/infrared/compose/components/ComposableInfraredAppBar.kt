package com.flipperdevices.infrared.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.keyscreen.shared.bar.ComposableBarBackIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitle
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar

@Composable
internal fun ComposableInfraredAppBar(
    name: String,
    isFavorite: Boolean,
    onShare: () -> Unit,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ComposableKeyScreenAppBar(
        modifier = modifier,
        startBlock = {
            ComposableBarBackIcon(it, onBack)
        },
        centerBlock = {
            ComposableBarTitle(
                text = name,
                modifier = it
            )
        },
        endBlock = {
            ComposableInfraredDropDown(
                modifier = it,
                isFavorite = isFavorite,
                onEdit = onEdit,
                onDelete = onDelete,
                onRename = onRename,
                onFavorite = onFavorite,
                onShare = onShare,
            )
        }
    )
}

