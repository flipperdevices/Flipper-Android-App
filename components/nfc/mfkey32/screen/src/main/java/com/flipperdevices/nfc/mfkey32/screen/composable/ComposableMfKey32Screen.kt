package com.flipperdevices.nfc.mfkey32.screen.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.nfc.mfkey32.screen.composable.output.AllKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.output.DuplicatedKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.output.UniqueKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.ComposableMfKey32Progress
import com.flipperdevices.nfc.mfkey32.screen.viewmodel.MfKey32ViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableMfKey32Screen(navController: NavController) {
    val viewModel = tangleViewModel<MfKey32ViewModel>()
    val state by viewModel.getMfKey32State().collectAsState()
    val foundedKeys by viewModel.getFoundedInformation().collectAsState()
    var isDisplayDialog by remember { mutableStateOf(false) }
    BackHandler {
        isDisplayDialog = true
    }
    if (isDisplayDialog) {
        val onContinue = {
            isDisplayDialog = false
        }
        val onAbort = {
            isDisplayDialog = false
            navController.popBackStack()
            Unit
        }
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
    Column {
        OrangeAppBar(
            titleId = R.string.mfkey32_title,
            onBack = { isDisplayDialog = true }
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ComposableMfKey32Progress(navController, state)
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
