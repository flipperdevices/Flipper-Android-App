package com.flipperdevices.updater.card.composable.dialogs

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import com.flipperdevices.core.ui.dialog.composable.FlipperDialogAndroid
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.dialog.composable.multichoice.setTitle
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun FlipperDialogReadyUpdate(
    isInstallUpdate: Boolean,
    version: FirmwareVersion,
    onDismiss: () -> Unit,
    onRun: () -> Unit
) {
    val titleId = if (isInstallUpdate) {
        R.string.update_card_confirm_install_title
    } else {
        R.string.update_card_confirm_update_title
    }

    val buttonId = if (isInstallUpdate) {
        R.string.update_card_confirm_install
    } else {
        R.string.update_card_confirm_update
    }

    val description = buildAnnotatedString {
        append(stringResource(R.string.update_card_confirm_desc_pre))
        append(' ')
        append(annotatedStringWithVersion(version))
        append(' ')
        append(stringResource(R.string.update_card_confirm_desc_post))
    }

    val dialogModel = remember(onDismiss, onRun) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(titleId)
            .setDescription(description)
            .setOnDismissRequest(onDismiss)
            .addButton(buttonId, onRun, isActive = true)
            .addButton(R.string.update_card_confirm_stop, onDismiss)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}

@Composable
fun FlipperDialogSynchronization(
    isInstallUpdate: Boolean,
    version: FirmwareVersion?,
    onCancel: () -> Unit,
    onPauseAndUpdate: () -> Unit
) {
    val buttonText = if (isInstallUpdate) {
        R.string.update_card_dialog_pause_sync_install
    } else {
        R.string.update_card_dialog_pause_sync_update
    }
    val description = buildAnnotatedString {
        if (isInstallUpdate) {
            append(stringResource(R.string.update_card_dialog_pause_sync_desc_pre_install))
        } else {
            append(stringResource(R.string.update_card_dialog_pause_sync_desc_pre_update))
        }
        append(' ')
        append(annotatedStringWithVersion(version))
        append(' ')
        append(stringResource(R.string.update_card_dialog_pause_sync_desc_post))
    }

    val dialogModel = remember(onPauseAndUpdate, onCancel) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.update_card_dialog_pause_sync_title)
            .setDescription(description)
            .setOnDismissRequest(onCancel)
            .addButton(
                buttonText,
                onPauseAndUpdate,
                isActive = true
            )
            .addButton(R.string.update_card_dialog_pause_sync_cancel, onCancel)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}

@Composable
fun FlipperDialogFileVeryBig(onDismiss: () -> Unit) {
    FlipperDialogAndroid(
        imageId = R.drawable.pic_wrong_file,
        titleId = R.string.update_card_dialog_file_big_title,
        textId = R.string.update_card_dialog_file_big_desc,
        buttonTextId = R.string.update_card_dialog_battery_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss
    )
}

@Composable
fun FlipperDialogFileExtension(onDismiss: () -> Unit) {
    FlipperDialogAndroid(
        imageId = R.drawable.pic_wrong_file,
        titleId = R.string.update_card_dialog_extension_title,
        textId = R.string.update_card_dialog_extension_desc,
        buttonTextId = R.string.update_card_dialog_battery_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss
    )
}

@Composable
fun FlipperDialogLowBattery(onDismiss: () -> Unit) {
    val imageId = if (MaterialTheme.colors.isLight) {
        R.drawable.pic_flipper_low_battery
    } else {
        R.drawable.pic_flipper_low_battery_dark
    }

    FlipperDialogAndroid(
        imageId = imageId,
        titleId = R.string.update_card_dialog_battery_title,
        textId = R.string.update_card_dialog_battery_desc,
        buttonTextId = R.string.update_card_dialog_battery_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss
    )
}
