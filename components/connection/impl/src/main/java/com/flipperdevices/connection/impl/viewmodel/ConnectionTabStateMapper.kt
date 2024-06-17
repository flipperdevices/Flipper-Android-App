package com.flipperdevices.connection.impl.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.theme.LocalPallet

object ConnectionTabStateMapper {
    @Composable
    @Suppress("LongMethod")
    fun getConnectionTabState(connectionState: ConnectionStatusState): TabState {
        return when (connectionState) {
            ConnectionStatusState.NoDevice -> TabState.Static(
                selectedIcon = R.drawable.ic_no_device_filled,
                notSelectedIcon = R.drawable.ic_no_device,
                text = stringResource(id = R.string.connection_status_no_device),
                selectedColor = LocalPallet.current.bottomBarSelectedFlipperStatus,
                selectedColorIcon = Color.Unspecified,
                unselectedColorIcon = Color.Unspecified,
                unselectedColor = LocalPallet.current.bottomBarUnselected
            )
            ConnectionStatusState.Disconnected -> TabState.Static(
                selectedIcon = R.drawable.ic_disconnected_filled,
                notSelectedIcon = R.drawable.ic_disconnected,
                text = stringResource(id = R.string.connection_status_not_connected),
                selectedColor = LocalPallet.current.bottomBarSelectedFlipperStatus,
                selectedColorIcon = Color.Unspecified,
                unselectedColorIcon = Color.Unspecified,
                unselectedColor = LocalPallet.current.bottomBarUnselected
            )
            ConnectionStatusState.Connecting -> TabState.Animated(
                selectedIcon = R.drawable.ic_connection_selected,
                selectedBackground = R.drawable.ic_connection_selected_bg,
                notSelectedIcon = R.drawable.ic_connection_unselected,
                notSelectedBackground = R.drawable.ic_connection_unselected_bg,
                text = stringResource(id = R.string.connection_status_connecting),
                selectedColor = LocalPallet.current.bottomBarSelectedFlipperStatus,
                selectedColorIcon = Color.Unspecified,
                unselectedColorIcon = Color.Unspecified,
                unselectedColor = LocalPallet.current.bottomBarUnselected,
                textDotsAnimated = true
            )
            ConnectionStatusState.Unsupported -> TabState.Static(
                selectedIcon = R.drawable.ic_no_device_filled,
                notSelectedIcon = R.drawable.ic_no_device,
                text = stringResource(id = R.string.connection_status_unsupported),
                selectedColor = LocalPallet.current.bottomBarUnsupported,
                selectedColorIcon = Color.Unspecified,
                unselectedColorIcon = Color.Unspecified,
                unselectedColor = LocalPallet.current.bottomBarUnselected,
            )
            ConnectionStatusState.Connected -> TabState.Static(
                selectedIcon = R.drawable.ic_connected_filled,
                notSelectedIcon = R.drawable.ic_connected,
                text = stringResource(id = R.string.connection_status_connected),
                selectedColor = LocalPallet.current.accentSecond,
                selectedColorIcon = Color.Unspecified,
                unselectedColorIcon = Color.Unspecified,
                unselectedColor = LocalPallet.current.bottomBarUnselected,
            )
            is ConnectionStatusState.Synchronization -> TabState.Animated(
                selectedIcon = R.drawable.ic_syncing_selected,
                selectedBackground = R.drawable.ic_syncing_selected_bg,
                notSelectedIcon = R.drawable.ic_syncing_unselected,
                notSelectedBackground = R.drawable.ic_syncing_unselected_bg,
                text = stringResource(
                    id =
                    R.string.connection_status_syncing,
                    connectionState.progress.roundPercentToString()
                ),
                selectedColor = LocalPallet.current.accentSecond,
                selectedColorIcon = Color.Unspecified,
                unselectedColorIcon = Color.Unspecified,
                unselectedColor = LocalPallet.current.bottomBarUnselected
            )
            is ConnectionStatusState.Synchronized -> TabState.Static(
                selectedIcon = R.drawable.ic_synced_filled,
                notSelectedIcon = R.drawable.ic_synced,
                text = stringResource(id = R.string.connection_status_synced),
                selectedColor = LocalPallet.current.accentSecond,
                selectedColorIcon = Color.Unspecified,
                unselectedColorIcon = Color.Unspecified,
                unselectedColor = LocalPallet.current.bottomBarUnselected,
            )
        }
    }
}
