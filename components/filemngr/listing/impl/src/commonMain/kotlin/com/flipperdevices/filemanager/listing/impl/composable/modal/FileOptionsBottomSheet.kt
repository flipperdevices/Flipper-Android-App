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
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel

@Composable
fun FileOptionsBottomSheet(
    fileOptionsSlot: Value<ChildSlot<*, PathWithType>>,
    slotNavigation: SlotNavigation<PathWithType>,
    selectionViewModel: SelectionViewModel,
    deleteFileViewModel: DeleteFilesViewModel,
    onDownloadFile: (PathWithType) -> Unit,
    onRename: (PathWithType) -> Unit,
    onMoveTo: (PathWithType) -> Unit,
    modifier: Modifier = Modifier
) {
    SlotModalBottomSheet(
        childSlotValue = fileOptionsSlot,
        onDismiss = { slotNavigation.dismiss() },
        content = { pathWithType ->
            BottomSheetOptionsContent(
                modifier = modifier.navigationBarsPadding(),
                fileType = pathWithType.fileType,
                path = pathWithType.fullPath,
                onCopyTo = {}, // todo
                onSelect = {
                    selectionViewModel.select(pathWithType)
                    slotNavigation.dismiss()
                },
                onRename = {
                    onRename.invoke(pathWithType)
                    slotNavigation.dismiss()
                },
                onExport = {
                    onDownloadFile.invoke(pathWithType)
                    slotNavigation.dismiss()
                },
                onDelete = {
                    deleteFileViewModel.tryDelete(pathWithType.fullPath)
                    slotNavigation.dismiss()
                },
                onMoveTo = {
                    onMoveTo.invoke(pathWithType)
                    slotNavigation.dismiss()
                }
            )
        }
    )
}
