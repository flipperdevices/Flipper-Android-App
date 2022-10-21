package com.flipperdevices.updater.card.composable.dialogs

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun FlipperDialogReadyUpdate(version: FirmwareVersion, onDismiss: () -> Unit, onRun: () -> Unit) {
    val text = buildAnnotatedStringWithVersionColor(
        version,
        postfixId = R.string.update_card_confirm_desc
    )
    val dialogModel = remember(onDismiss, onRun) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.update_card_confirm_title)
            .setDescription(text)
            .setOnDismissRequest(onDismiss)
            .addButton(R.string.update_card_confirm_cont, onRun, isActive = true)
            .addButton(R.string.update_card_confirm_stop, onDismiss)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}

@Composable
fun FlipperDialogSynchronization(onContinue: () -> Unit, onPause: () -> Unit) {
    val dialogModel = remember(onContinue, onPause) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.update_card_sync_title)
            .setDescription(R.string.update_card_sync_desc)
            .setOnDismissRequest(onContinue)
            .addButton(R.string.update_card_sync_cont, onContinue, isActive = true)
            .addButton(R.string.update_card_sync_stop, onPause)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}

@Composable
fun FlipperDialogFileVeryBig(onDismiss: () -> Unit) {
    val imageId = if (MaterialTheme.colors.isLight) {
        DesignSystem.drawable.ic_firmware_application_deprecated
    } else DesignSystem.drawable.ic_firmware_application_deprecated_dark

    FlipperDialog(
        imageId = imageId,
        titleId = R.string.update_card_dialog_file_big_title,
        textId = R.string.update_card_dialog_file_big_desc,
        buttonTextId = R.string.update_card_dialog_battery_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss
    )
}

@Composable
fun FlipperDialogFileExtension(onDismiss: () -> Unit) {
    val imageId = if (MaterialTheme.colors.isLight) {
        DesignSystem.drawable.ic_firmware_application_deprecated
    } else DesignSystem.drawable.ic_firmware_application_deprecated_dark

    FlipperDialog(
        imageId = imageId,
        titleId = R.string.update_card_dialog_extension_title,
        textId = R.string.update_card_dialog_extension_desc,
        buttonTextId = R.string.update_card_dialog_battery_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss
    )
}

@Composable
fun FlipperDialogLowBattery(onDismiss: () -> Unit) {
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
