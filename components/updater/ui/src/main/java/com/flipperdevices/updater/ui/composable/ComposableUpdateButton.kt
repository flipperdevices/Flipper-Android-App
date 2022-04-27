package com.flipperdevices.updater.ui.composable

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.ui.R
import com.flipperdevices.updater.ui.viewmodel.UpdaterViewModel

@Composable
fun ComposableUpdateButton(
    updateCardState: UpdateCardState
) {
    val updaterViewModel = viewModel<UpdaterViewModel>()

    val updatingState by updaterViewModel.getState().collectAsState()
    val updatingStateLocal: UpdatingState = updatingState
    when (updatingStateLocal) {
        UpdatingState.NotStarted -> {}
        is UpdatingState.DownloadingFromNetwork -> {
            ComposableInProgressIndicator(
                updaterViewModel = updaterViewModel,
                accentColorId = R.color.update_green,
                secondColorId = R.color.update_green_background,
                iconId = R.drawable.ic_globe,
                percent = updatingStateLocal.percent,
                descriptionId = R.string.update_stage_downloading_desc
            )
            return
        }
        is UpdatingState.UploadOnFlipper -> {
            ComposableInProgressIndicator(
                updaterViewModel = updaterViewModel,
                accentColorId = DesignSystem.color.accent_secondary,
                secondColorId = R.color.update_blue_background,
                iconId = R.drawable.ic_bluetooth,
                percent = updatingStateLocal.percent,
                descriptionId = R.string.update_stage_uploading_desc
            )
            return
        }
        UpdatingState.Rebooting -> {
            Text("Rebooting now")
            return
        }
    }
}
