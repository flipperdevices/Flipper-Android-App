package com.flipperdevices.keyscreen.impl.composable.view.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.ShareState

@Composable
fun ComposableShare(shareState: ShareState, onShare: () -> Unit) {
    when (shareState) {
        ShareState.PROGRESS -> ComposableActionRowInProgress(R.string.keyscreen_share_text)
        ShareState.NOT_SHARING -> ComposableActionRow(
            iconId = R.drawable.ic_upload,
            descriptionId = R.string.keyscreen_share_text,
            onClick = onShare
        )
    }
}
