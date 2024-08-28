package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.mapping

import com.flipperdevices.infrared.api.InfraredConnectionApi
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.viewmodel.RemoteGridViewModel

internal object GridComponentStateMapper {
    fun map(
        saveState: SaveTempSignalApi.State,
        gridState: RemoteGridViewModel.State,
        dispatchState: DispatchSignalApi.State,
        connectionState: InfraredConnectionApi.InfraredEmulateState
    ): RemoteGridComponent.Model = when (gridState) {
        RemoteGridViewModel.State.Error -> RemoteGridComponent.Model.Error
        is RemoteGridViewModel.State.Loaded -> {
            when (saveState) {
                SaveTempSignalApi.State.Error -> RemoteGridComponent.Model.Error
                is SaveTempSignalApi.State.Uploading,
                SaveTempSignalApi.State.Uploaded,
                SaveTempSignalApi.State.Pending -> {
                    RemoteGridComponent.Model.Loaded(
                        pagesLayout = gridState.pagesLayout,
                        remotes = gridState.remotes,
                        isFlipperBusy = dispatchState is DispatchSignalApi.State.FlipperIsBusy,
                        emulatedKey = (dispatchState as? DispatchSignalApi.State.Emulating)?.ifrKeyIdentifier,
                        saveState = saveState,
                        connectionState = connectionState
                    )
                }
            }
        }

        RemoteGridViewModel.State.Loading -> RemoteGridComponent.Model.Loading(0f)
    }
}
