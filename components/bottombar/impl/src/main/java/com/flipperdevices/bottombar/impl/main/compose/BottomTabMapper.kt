package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.api.api.ConnectionApi
import com.flipperdevices.connection.api.model.ConnectionStatusState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun getTabStateFromFlipperBottomTab(
    connectionApi: ConnectionApi,
    bottomTab: FlipperBottomTab
): TabState {
    return when (bottomTab) {
        FlipperBottomTab.DEVICE -> {
            val connectionStatusState by connectionApi.getConnectionTabState().collectAsState()
            return getConnectionTabState(connectionStatusState)
        }
        FlipperBottomTab.ARCHIVE -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_archive_selected,
            notSelectedIcon = DesignSystem.drawable.ic_archive_unselected,
            text = stringResource(R.string.bar_title_archive),
            selectedColor = LocalPallet.current.selectedBottomBar,
            unselectedColor = LocalPallet.current.unselectedBottomBar30
        )
        FlipperBottomTab.OPTIONS -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_options_selected,
            notSelectedIcon = DesignSystem.drawable.ic_options_unselected,
            text = stringResource(R.string.bar_title_options),
            selectedColor = LocalPallet.current.selectedBottomBar,
            unselectedColor = LocalPallet.current.unselectedBottomBar30
        )
    }
}

@Composable
fun getConnectionTabState(connectionState: ConnectionStatusState): TabState {
    return when (connectionState) {
        ConnectionStatusState.NoDevice -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_no_device_filled,
            notSelectedIcon = DesignSystem.drawable.ic_no_device,
            text = stringResource(id = R.string.connection_status_no_device),
            selectedColor = LocalPallet.current.unselectedBottomBar40,
            unselectedColor = LocalPallet.current.unselectedBottomBar30
        )
        ConnectionStatusState.Disconnected -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_disconnected_filled,
            notSelectedIcon = DesignSystem.drawable.ic_disconnected,
            text = stringResource(id = R.string.connection_status_not_connected),
            selectedColor = LocalPallet.current.unselectedBottomBar40,
            unselectedColor = LocalPallet.current.unselectedBottomBar30
        )
        ConnectionStatusState.Connecting -> TabState.Animated(
            selectedIcon = DesignSystem.raw.ic_connecting_filled,
            notSelectedIcon = DesignSystem.raw.ic_connecting,
            text = stringResource(id = R.string.connection_status_connecting),
            selectedColor = LocalPallet.current.unselectedBottomBar40,
            unselectedColor = LocalPallet.current.unselectedBottomBar30,
            textDotsAnimated = true
        )
        ConnectionStatusState.Unsupported -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_no_device_filled,
            notSelectedIcon = DesignSystem.drawable.ic_no_device,
            text = stringResource(id = R.string.connection_status_unsupported),
            selectedColor = LocalPallet.current.redContentBottomBar,
            unselectedColor = LocalPallet.current.unselectedBottomBar30,
            unselectedColorIcon = LocalPallet.current.redContentBottomBar
        )
        ConnectionStatusState.Connected -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_connected_filled,
            notSelectedIcon = DesignSystem.drawable.ic_connected,
            text = stringResource(id = R.string.connection_status_connected),
            selectedColor = LocalPallet.current.accentSecond,
            unselectedColor = LocalPallet.current.unselectedBottomBar30,
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
            unselectedColor = LocalPallet.current.unselectedBottomBar30
        )
        is ConnectionStatusState.Synchronized -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_synced_filled,
            notSelectedIcon = DesignSystem.drawable.ic_synced,
            text = stringResource(id = R.string.connection_status_synced),
            selectedColor = LocalPallet.current.accentSecond,
            unselectedColor = LocalPallet.current.unselectedBottomBar30,
            unselectedColorIcon = LocalPallet.current.accentSecond
        )
    }
}
