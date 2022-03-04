package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.DeleteState

@Composable
fun ComposableDelete(deleteState: DeleteState, onClick: () -> Unit) {
    if (deleteState == DeleteState.PROGRESS) {
        ComposableActionRowInProgress(
            descriptionId = R.string.keyscreen_deleting_text,
            descriptionColorId = R.color.keyscreen_trash_color
        )
        return
    }

    ComposableActionRow(
        iconId = R.drawable.ic_trash_icon,
        tintId = R.color.keyscreen_trash_color,
        descriptionId = R.string.keyscreen_delete_text,
        descriptionColorId = R.color.keyscreen_trash_color,
        onClick = onClick
    )
}
