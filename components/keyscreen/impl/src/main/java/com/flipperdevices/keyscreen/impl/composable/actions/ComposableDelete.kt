package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.DeleteState

@Composable
fun ComposableDelete(deleteState: DeleteState, onClick: () -> Unit) {
    if (deleteState == DeleteState.PROGRESS) {
        ComposableActionRowInProgress(
            descriptionId = R.string.keyscreen_deleting_text,
            descriptionColor = LocalPallet.current.redForgot
        )
        return
    }

    val descriptionId = if (deleteState == DeleteState.DELETED) {
        R.string.keyscreen_delete_permanently_text
    } else R.string.keyscreen_delete_text

    ComposableActionRow(
        iconId = DesignSystem.drawable.ic_trash_icon,
        tint = LocalPallet.current.redForgot,
        descriptionId = descriptionId,
        descriptionColor = LocalPallet.current.redForgot,
        onClick = onClick
    )
}
