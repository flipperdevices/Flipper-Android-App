package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.hexkeyboard.ComposableHexKeyboard
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.shared.bar.ComposableBarBackIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableBarSimpleText
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitle
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitleWithName
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar
import com.flipperdevices.nfceditor.impl.R
import com.flipperdevices.nfceditor.impl.composable.dialog.ComposableNfcEditExitDialog
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel

private const val KEYBOARD_HEIGHT_DP = 256

@Composable
fun ComposableNfcEditorScreen(
    nfcEditorViewModel: NfcEditorViewModel,
    modifier: Modifier = Modifier
) {
    val nfcEditorState by nfcEditorViewModel.getNfcEditorState().collectAsState()
    val router = LocalRouter.current
    val localNfcEditorState = nfcEditorState

    if (localNfcEditorState == null) {
        LaunchedEffect(key1 = localNfcEditorState) {
            router.exit()
        }
        return
    }
    val showOnSaveDialog by nfcEditorViewModel.getShowOnSaveDialogState().collectAsState()
    if (showOnSaveDialog) {
        ComposableNfcEditExitDialog(
            onDismiss = nfcEditorViewModel::dismissDialog,
            onNotSave = { router.exit() },
            onSave = { nfcEditorViewModel.onSave(router) },
            onSaveAs = { nfcEditorViewModel.onSaveAs(router) }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        ComposableNfcEditorBar(localNfcEditorState.cardName, onBack = {
            nfcEditorViewModel.onBack(router)
        }, onSave = {
                nfcEditorViewModel.onSave(router)
            }, onSaveAs = {
                nfcEditorViewModel.onSaveAs(router)
            })
        ComposableNfcEditor(
            modifier = Modifier.weight(1f),
            nfcEditorViewModel = nfcEditorViewModel,
            nfcEditorState = localNfcEditorState
        )

        var offsetForKeyboard by remember { mutableStateOf(KEYBOARD_HEIGHT_DP.dp) }
        if (nfcEditorViewModel.currentActiveCell != null) {
            offsetForKeyboard = 0.dp
            val offset by animateDpAsState(offsetForKeyboard)
            ComposableHexKeyboard(
                modifier = Modifier
                    .offset(y = offset)
                    .background(LocalPallet.current.hexKeyboardBackground),
                keyboardHeight = KEYBOARD_HEIGHT_DP.dp,
                onClick = nfcEditorViewModel::onKeyInput
            )
        } else {
            offsetForKeyboard = KEYBOARD_HEIGHT_DP.dp
        }
    }
}

@Composable
private fun ComposableSaveDropDown(
    isVisible: Boolean,
    onSave: () -> Unit,
    onSaveAs: () -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = isVisible, onDismissRequest = onDismiss) {
        DropdownMenuItem(onClick = {
            onSave()
            onDismiss()
        }) {
            Text(text = stringResource(R.string.nfc_popup_save))
        }
        DropdownMenuItem(onClick = {
            onSaveAs()
            onDismiss()
        }) {
            Text(text = stringResource(R.string.nfc_popup_save_as))
        }
    }
}

@Composable
private fun ComposableNfcEditorBar(
    keyName: String?,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onSaveAs: () -> Unit
) {
    ComposableKeyScreenAppBar(
        startBlock = {
            ComposableBarBackIcon(it, onBack)
        },
        centerBlock = {
            if (keyName == null) {
                ComposableBarTitle(
                    modifier = it,
                    textId = R.string.nfceditor_title
                )
            } else {
                ComposableBarTitleWithName(
                    modifier = it,
                    titleId = R.string.nfceditor_title,
                    name = keyName
                )
            }
        },
        endBlock = {
            var dropDownVisible by remember {
                mutableStateOf(false)
            }
            Box(modifier = it) {
                ComposableBarSimpleText(
                    modifier = Modifier,
                    text = stringResource(R.string.nfceditor_btn_save),
                    onClick = { dropDownVisible = true }
                )
                ComposableSaveDropDown(dropDownVisible, onSave, onSaveAs, onDismiss = {
                    dropDownVisible = false
                })
            }
        }
    )
}
