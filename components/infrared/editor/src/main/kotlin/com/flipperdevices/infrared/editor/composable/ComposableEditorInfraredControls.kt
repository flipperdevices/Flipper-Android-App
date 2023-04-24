package com.flipperdevices.infrared.editor.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.editor.composable.dragdrop.DragDropColumn
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import com.flipperdevices.keyscreen.api.Picture

@Composable
internal fun ComposableEditorInfraredControls(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    keyEmulateUiApi: KeyEmulateUiApi,
    state: InfraredEditorState.LoadedKey,
    onChangePosition: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComposableInfraredAppBar(
            onCancel = onCancel,
            onSave = onSave,
            name = state.name
        )

        DragDropColumn(
            items = state.remotes,
            onSwap = onChangePosition,
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            keyEmulateUiApi.ComposableEmulateButtonWithText(
                modifier = Modifier,
                buttonModifier = Modifier,
                progress = null,
                buttonText = it.name,
                textId = null,
                iconId = null,
                picture = Picture.StaticRes(R.drawable.ic_burger),
                color = LocalPallet.current.actionOnFlipperInfraredEnable,
                progressColor = Color.Transparent,
            )
        }
    }
}
