package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.keyscreen.shared.bar.ComposableBarBackIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableBarSimpleText
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitle
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitleWithName
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar
import com.flipperdevices.nfceditor.impl.R
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel

@Composable
fun ComposableNfcEditorScreen(nfcEditorViewModel: NfcEditorViewModel) {
    val nfcEditorState by nfcEditorViewModel.getNfcEditorState().collectAsState()
    val router = LocalRouter.current
    val localNfcEditorState = nfcEditorState

    if (localNfcEditorState == null) {
        LaunchedEffect(key1 = localNfcEditorState) {
            router.exit()
        }
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        ComposableNfcEditorBar(localNfcEditorState.cardName, onBack = {
            router.exit()
        }, onSave = { nfcEditorViewModel.onSave(router) })

        ComposableNfcEditor(
            nfcEditorViewModel = nfcEditorViewModel,
            nfcEditorState = localNfcEditorState
        )
    }
}

@Composable
private fun ComposableNfcEditorBar(keyName: String?, onBack: () -> Unit, onSave: () -> Unit) {
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
            } else ComposableBarTitleWithName(
                modifier = it,
                titleId = R.string.nfceditor_title,
                name = keyName
            )
        },
        endBlock = {
            ComposableBarSimpleText(
                modifier = it,
                text = stringResource(R.string.nfceditor_btn_save),
                onClick = onSave
            )
        }
    )
}
