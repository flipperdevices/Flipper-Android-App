package com.flipperdevices.infrared.editor.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.editor.compose.ComposableInfraredEditorDialog
import com.flipperdevices.infrared.editor.compose.components.ComposableInfraredEditorAppBar
import com.flipperdevices.infrared.editor.compose.components.ComposableInfraredEditorItem
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
internal fun ComposableInfraredEditorScreenReady(
    keyState: InfraredEditorState.Ready,
    dialogState: Boolean,
    onDoNotSave: () -> Unit,
    onDismissDialog: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onTapRemote: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onEditOrder: (Int, Int) -> Unit,
) {
    ComposableInfraredEditorDialog(
        isShow = dialogState,
        onSave = onSave,
        onDoNotSave = onDoNotSave,
        onDismissDialog = onDismissDialog
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalPallet.current.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComposableInfraredEditorAppBar(
            keyName = keyState.keyName,
            onCancel = onCancel,
            onSave = onSave
        )

        val state = rememberReorderableLazyListState(onMove = { from, to ->
            onEditOrder(from.index, to.index)
        })

        LazyColumn(
            state = state.listState,
            contentPadding = PaddingValues(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .reorderable(state)
        ) {
            itemsIndexed(items = keyState.remotes) { index, remote ->
                ReorderableItem(
                    state = state,
                    defaultDraggingModifier = Modifier,
                    key = remote,
                    index = index,
                ) {
                    ComposableInfraredEditorItem(
                        remoteName = remote.name,
                        onTap = { onTapRemote(index) },
                        onDelete = { onDelete(index) },
                        dragModifier = Modifier.detectReorderAfterLongPress(state)
                    )
                }
            }
        }
    }
}
