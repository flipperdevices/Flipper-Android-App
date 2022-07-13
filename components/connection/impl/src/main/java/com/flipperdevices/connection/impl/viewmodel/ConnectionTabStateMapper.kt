package com.flipperdevices.connection.impl.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet

object ConnectionTabStateMapper {
    @Composable
    @Suppress("LongMethod")
    fun getConnectionTabState(connectionState: ConnectionStatusState): TabState {
        return when (connectionState) {
            ConnectionStatusState.NoDevice -> TabState.Static(
                selectedIcon = DesignSystem.drawable.ic_no_device_filled,
                notSelectedIcon = DesignSystem.drawable.ic_no_device,
                text = stringResource(id = R.string.connection_status_no_device),
                selectedColor = LocalPallet.current.bottomBarSelectedFlipperStatus,
                unselectedColor = LocalPallet.current.bottomBarUnselected
            )
            ConnectionStatusState.Disconnected -> TabState.Static(
                selectedIcon = DesignSystem.drawable.ic_disconnected_filled,
                notSelectedIcon = DesignSystem.drawable.ic_disconnected,
                text = stringResource(id = R.string.connection_status_not_connected),
                selectedColor = LocalPallet.current.bottomBarSelectedFlipperStatus,
                unselectedColor = LocalPallet.current.bottomBarUnselected
            )
            ConnectionStatusState.Connecting -> TabState.Animated(
                selectedIcon = DesignSystem.raw.ic_connecting_filled,
                notSelectedIcon = DesignSystem.raw.ic_connecting,
                text = stringResource(id = R.string.connection_status_connecting),
                selectedColor = LocalPallet.current.bottomBarSelectedFlipperStatus,
                unselectedColor = LocalPallet.current.bottomBarUnselected,
                textDotsAnimated = true
            )
            ConnectionStatusState.Unsupported -> TabState.Static(
                selectedIcon = DesignSystem.drawable.ic_no_device_filled,
                notSelectedIcon = DesignSystem.drawable.ic_no_device,
                text = stringResource(id = R.string.connection_status_unsupported),
                selectedColor = LocalPallet.current.bottomBarUnsupported,
                unselectedColor = LocalPallet.current.bottomBarUnselected,
                unselectedColorIcon = LocalPallet.current.bottomBarUnsupported
            )
            ConnectionStatusState.Connected -> TabState.Static(
                selectedIcon = DesignSystem.drawable.ic_connected_filled,
                notSelectedIcon = DesignSystem.drawable.ic_connected,
                text = stringResource(id = R.string.connection_status_connected),
                selectedColor = LocalPallet.current.accentSecond,
                unselectedColor = LocalPallet.current.bottomBarUnselected,
                unselectedColorIcon = LocalPallet.current.accentSecond
            )
            is ConnectionStatusState.Synchronization -> TabState.Animated(
                selectedIcon = DesignSystem.raw.ic_syncing_filled,
                notSelectedIcon = DesignSystem.raw.ic_syncing,
                text = stringResource(
                    id =
                    R.string.connection_status_syncing,
                    connectionState.progress.roundPercentToString()
                ),
                selectedColor = LocalPallet.current.accentSecond,
                unselectedColor = LocalPallet.current.bottomBarUnselected
            )
            is ConnectionStatusState.Synchronized -> TabState.Static(
                selectedIcon = DesignSystem.drawable.ic_synced_filled,
                notSelectedIcon = DesignSystem.drawable.ic_synced,
                text = stringResource(id = R.string.connection_status_synced),
                selectedColor = LocalPallet.current.accentSecond,
                unselectedColor = LocalPallet.current.bottomBarUnselected,
                unselectedColorIcon = LocalPallet.current.accentSecond
            )
        }
    }
}
