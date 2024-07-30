package com.flipperdevices.remotecontrols.impl.grid.presentation.mapping

import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel

internal object GridComponentStateMapper {
    fun map(
        saveState: SaveTempSignalApi.State,
        gridState: GridViewModel.State,
        dispatchState: DispatchSignalApi.State
    ): GridComponent.Model = when (gridState) {
        GridViewModel.State.Error -> GridComponent.Model.Error
        is GridViewModel.State.Loaded -> {
            when (saveState) {
                SaveTempSignalApi.State.Error -> GridComponent.Model.Error
                SaveTempSignalApi.State.Uploaded, SaveTempSignalApi.State.Pending -> {
                    GridComponent.Model.Loaded(
                        pagesLayout = gridState.pagesLayout,
                        remotes = gridState.remotes,
                        isFlipperBusy = dispatchState is DispatchSignalApi.State.FlipperIsBusy,
                        emulatedKey = (dispatchState as? DispatchSignalApi.State.Emulating)?.ifrKeyIdentifier,
                        isDownloaded = gridState.isDownloaded
                    )
                }

                is SaveTempSignalApi.State.Uploading -> GridComponent.Model.Loading(
                    saveState.progress
                )
            }
        }

        GridViewModel.State.Loading -> GridComponent.Model.Loading(0f)
    }
}
