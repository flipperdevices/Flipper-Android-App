package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.common.ComposableActionRow
import com.flipperdevices.keyscreen.impl.composable.actions.common.ComposableActionRowInProgress
import com.flipperdevices.keyscreen.model.ShareState
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableShare(shareState: ShareState, onShare: () -> Unit) {
    when (shareState) {
        ShareState.PROGRESS -> ComposableActionRowInProgress(R.string.keyscreen_share_text)
        ShareState.NOT_SHARING -> ComposableActionRow(
            iconId = DesignSystem.drawable.ic_upload,
            descriptionId = R.string.keyscreen_share_text,
            onClick = onShare
        )
    }
}
