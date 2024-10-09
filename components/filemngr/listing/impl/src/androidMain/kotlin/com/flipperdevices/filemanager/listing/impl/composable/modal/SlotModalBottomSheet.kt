package com.flipperdevices.filemanager.listing.impl.composable.modal

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.flipperdevices.filemanager.listing.impl.composable.modal.util.zero
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop

@Composable
fun <T : Any, K : Any> SlotModalBottomSheet(
    childSlotValue: Value<ChildSlot<T, K>>,
    onDismiss: () -> Unit,
    skipPartiallyExpanded: Boolean = true,
    content: @Composable ColumnScope.(K) -> Unit
) {
    val childSlot by childSlotValue.subscribeAsState()
    SlotModalBottomSheet(
        childSlot = childSlot,
        skipPartiallyExpanded = skipPartiallyExpanded,
        onDismiss = onDismiss,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any, K : Any> SlotModalBottomSheet(
    childSlot: ChildSlot<T, K>,
    onDismiss: () -> Unit,
    skipPartiallyExpanded: Boolean = true,
    content: @Composable ColumnScope.(K) -> Unit
) {
    val child = childSlot.child?.instance
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = {
            true
        }
    )

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isVisible }
            .distinctUntilChanged()
            .drop(1)
            .collect { visible ->
                if (!visible) {
                    onDismiss.invoke()
                }
            }
    }

    if (child != null) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss.invoke() },
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets.zero },
            content = {
                content.invoke(this, child)
            },
            containerColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
        )
    }
}
