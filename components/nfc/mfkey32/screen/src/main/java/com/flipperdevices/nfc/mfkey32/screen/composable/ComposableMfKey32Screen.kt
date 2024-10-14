package com.flipperdevices.nfc.mfkey32.screen.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.dialog.composable.multichoice.setDescription
import com.flipperdevices.core.ui.dialog.composable.multichoice.setTitle
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.nfc.mfkey32.screen.composable.output.AllKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.output.DuplicatedKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.output.UniqueKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.ComposableMfKey32Progress
import com.flipperdevices.nfc.mfkey32.screen.model.FoundedInformation
import com.flipperdevices.nfc.mfkey32.screen.model.MfKey32State

@Composable
fun ComposableMfKey32Screen(
    onBack: () -> Unit,
    foundedKeys: FoundedInformation,
    state: MfKey32State,
    flipperColor: HardwareColor,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        OrangeAppBar(
            titleId = R.string.mfkey32_title,
            onBack = onBack
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ComposableMfKey32Progress(onBack, state, flipperColor)
            }
            if (foundedKeys.keys.isNotEmpty()) {
                AllKeys(foundedKeys.keys)
            }
            if (foundedKeys.uniqueKeys.isNotEmpty()) {
                UniqueKeys(foundedKeys.uniqueKeys)
            }
            if (foundedKeys.duplicated.isNotEmpty()) {
                DuplicatedKeys(foundedKeys.duplicated)
            }
        }
    }
}

@Composable
fun ComposableMfKey32Dialog(
    onContinue: () -> Unit,
    onAbort: () -> Unit
) {
    val dialogModel = remember(onContinue, onAbort) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.nfc_reader_tool_dialog_title)
            .setDescription(R.string.nfc_reader_tool_dialog_desc)
            .setOnDismissRequest(onContinue)
            .addButton(
                R.string.nfc_reader_tool_dialog_abort,
                onAbort,
                isActive = true
            )
            .addButton(R.string.nfc_reader_tool_dialog_cont, onContinue)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}
