package com.flipperdevices.faphub.installedtab.impl.composable.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.faphub.installedtab.impl.model.FapBatchUpdateButtonState

@Composable
fun ComposableUpdateAllButton(
    state: FapBatchUpdateButtonState,
    onUpdateAll: () -> Unit,
    onCancelAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        FapBatchUpdateButtonState.Offline -> {
            ComposableOfflineButtonText(modifier)
        }

        FapBatchUpdateButtonState.NoUpdates -> {}

        FapBatchUpdateButtonState.Loading -> {
            ComposableCancelAllButton(
                modifier.placeholderConnecting()
            )
        }

        is FapBatchUpdateButtonState.ReadyToUpdate -> {
            ComposableUpdateAllButtonPending(
                pendingCount = state.count,
                modifier = modifier.clickableRipple(onClick = onUpdateAll)
            )
        }

        FapBatchUpdateButtonState.UpdatingInProgress -> {
            ComposableCancelAllButton(
                modifier = modifier.clickableRipple(onClick = onCancelAll)
            )
        }
    }
}
