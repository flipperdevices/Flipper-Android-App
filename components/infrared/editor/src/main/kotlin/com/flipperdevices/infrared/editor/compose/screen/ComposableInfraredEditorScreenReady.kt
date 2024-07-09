package com.flipperdevices.infrared.editor.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    onChangeName: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onEditOrder: (Int, Int) -> Unit,
    onChangeIndexEditor: (Int) -> Unit,
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

        LaunchedEffect(keyState) {
            val firstErrorRemote = keyState.errorRemotes.firstOrNull() ?: return@LaunchedEffect
            state.listState.scrollToItem(firstErrorRemote)
        }

        LazyColumn(
            state = state.listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
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
                ) { _ ->
                    ComposableInfraredEditorItem(
                        remoteName = remote.name,
                        onChangeName = { onChangeName(index, it) },
                        onDelete = { onDelete(index) },
                        dragModifier = Modifier.detectReorderAfterLongPress(state),
                        onChangeIndexEditor = { onChangeIndexEditor(index) },
                        isActive = keyState.activeRemote == index,
                        isError = index in keyState.errorRemotes,
                    )
                }
            }
            item { Box(modifier = Modifier.navigationBarsPadding()) }
        }
    }
}
