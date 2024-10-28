package com.flipperdevices.filemanager.listing.impl.composable.modal

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.EditFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel

@Composable
fun FileOptionsBottomSheet(
    createFileViewModel: EditFileViewModel,
    fileOptionsSlot: Value<ChildSlot<*, PathWithType>>,
    slotNavigation: SlotNavigation<PathWithType>,
    selectionViewModel: SelectionViewModel,
    deleteFileViewModel: DeleteFilesViewModel,
    modifier: Modifier = Modifier
) {
    SlotModalBottomSheet(
        childSlotValue = fileOptionsSlot,
        onDismiss = { slotNavigation.dismiss() },
        content = {
            BottomSheetOptionsContent(
                modifier = modifier.navigationBarsPadding(),
                fileType = it.fileType,
                path = it.fullPath,
                onCopyTo = {}, // todo
                onSelect = {
                    selectionViewModel.select(it)
                    slotNavigation.dismiss()
                },
                onRename = {
                    createFileViewModel.onRename(it)
                    slotNavigation.dismiss()
                },
                onExport = {}, // todo
                onDelete = {
                    deleteFileViewModel.tryDelete(it.fullPath)
                    slotNavigation.dismiss()
                },
                onMoveTo = {} // todo
            )
        }
    )
}
