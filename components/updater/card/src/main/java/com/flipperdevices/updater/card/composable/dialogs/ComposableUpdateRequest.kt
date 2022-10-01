package com.flipperdevices.updater.card.composable.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.updater.card.model.UpdatePending
import com.flipperdevices.updater.card.model.UpdatePendingState
import com.flipperdevices.updater.card.viewmodel.UpdateRequestViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableUpdateRequest(
    updateRequestViewModel: UpdateRequestViewModel = tangleViewModel(),
    pendingUpdateRequest: UpdatePending,
    onDismiss: () -> Unit
) {
    val updatePendingState by updateRequestViewModel.getUpdatePendingState().collectAsState()
    val localUpdatePendingState = updatePendingState
    println("UpdatePendingState: $updatePendingState")

    LaunchedEffect(key1 = pendingUpdateRequest) {
        println("LaunchedEffect")
        updateRequestViewModel.onUpdateRequest(pendingUpdateRequest)
    }

    val localDismiss = {
        onDismiss()
        updateRequestViewModel.resetState()
    }

    when (localUpdatePendingState) {
        UpdatePendingState.FileExtension -> FlipperDialogFileExtension(localDismiss)
        UpdatePendingState.FileBig -> FlipperDialogFileVeryBig(localDismiss)
        UpdatePendingState.LowBattery -> FlipperDialogLowBattery(localDismiss)
        is UpdatePendingState.Ready -> {
            val version = localUpdatePendingState.request.updateTo
            if (localUpdatePendingState.isSyncing) {
                FlipperDialogSynchronization(localDismiss) {
                    updateRequestViewModel.stopSyncAndStartUpdate(localUpdatePendingState.request)
                }
            } else FlipperDialogReadyUpdate(version, localDismiss) {
                updateRequestViewModel.openUpdate(localUpdatePendingState.request)
                localDismiss()
                return@FlipperDialogReadyUpdate
            }
        }
        null -> {}
    }
}
