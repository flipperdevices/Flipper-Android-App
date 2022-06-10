package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.DeleteState

@Composable
fun ComposableDelete(deleteState: DeleteState, onClick: () -> Unit) {
    if (deleteState == DeleteState.PROGRESS) {
        ComposableActionRowInProgress(
            descriptionId = R.string.keyscreen_deleting_text,
            descriptionColorId = DesignSystem.color.red
        )
        return
    }

    val descriptionId = if (deleteState == DeleteState.DELETED) {
        R.string.keyscreen_delete_permanently_text
    } else R.string.keyscreen_delete_text

    ComposableActionRow(
        iconId = R.drawable.ic_trash_icon,
        tintId = DesignSystem.color.red,
        descriptionId = descriptionId,
        descriptionColorId = DesignSystem.color.red,
        onClick = onClick
    )
}
