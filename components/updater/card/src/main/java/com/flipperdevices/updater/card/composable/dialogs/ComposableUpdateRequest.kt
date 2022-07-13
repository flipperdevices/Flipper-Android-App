package com.flipperdevices.updater.card.composable.dialogs

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.viewmodel.UpdateRequestViewModel
import com.flipperdevices.updater.model.UpdateCardState

@Composable
fun ComposableUpdateRequest(
    updateRequestViewModel: UpdateRequestViewModel = viewModel(),
    pendingUpdateRequest: UpdateCardState.UpdateAvailable,
    onDismiss: () -> Unit
) {
    val batteryState by updateRequestViewModel.getBatteryState().collectAsState()

    if (batteryState.isAllowToUpdate()) {
        updateRequestViewModel.openUpdate(pendingUpdateRequest)
        onDismiss()
        return
    }

    val imageId = if (MaterialTheme.colors.isLight) DesignSystem.drawable.pic_flipper_low_battery
    else DesignSystem.drawable.pic_flipper_low_battery_dark

    FlipperDialog(
        imageId = imageId,
        titleId = R.string.update_card_dialog_battery_title,
        textId = R.string.update_card_dialog_battery_desc,
        buttonTextId = R.string.update_card_dialog_battery_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss
    )
}
