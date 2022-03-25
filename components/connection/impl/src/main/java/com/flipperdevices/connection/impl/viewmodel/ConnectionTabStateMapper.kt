package com.flipperdevices.connection.impl.viewmodel

import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.ui.R as DesignSystem

object ConnectionTabStateMapper {
    fun getConnectionTabState(connectionState: ConnectionStatusState): TabState {
        return when (connectionState) {
            ConnectionStatusState.NoDevice -> TabState.Static(
                selectedIcon = R.drawable.ic_no_device_filled,
                notSelectedIcon = R.drawable.ic_no_device,
                textId = R.string.connection_status_no_device,
                selectedColor = DesignSystem.color.black_40,
                unselectedColor = DesignSystem.color.black_30
            )
            ConnectionStatusState.Disconnected -> TabState.Static(
                selectedIcon = R.drawable.ic_disconnected_filled,
                notSelectedIcon = R.drawable.ic_disconnected,
                textId = R.string.connection_status_not_connected,
                selectedColor = DesignSystem.color.black_40,
                unselectedColor = DesignSystem.color.black_30
            )
            ConnectionStatusState.Connecting -> TabState.Animated(
                selectedIcon = R.raw.ic_connecting_filled,
                notSelectedIcon = R.raw.ic_connecting,
                textId = R.string.connection_status_connecting,
                selectedColor = DesignSystem.color.black_40,
                unselectedColor = DesignSystem.color.black_30,
                textDotsAnimated = true
            )
            ConnectionStatusState.Unsupported -> TabState.Static(
                selectedIcon = R.drawable.ic_no_device_filled,
                notSelectedIcon = R.drawable.ic_no_device,
                textId = R.string.connection_status_unsupported,
                selectedColor = DesignSystem.color.red,
                unselectedColor = DesignSystem.color.black_30,
                unselectedColorIcon = DesignSystem.color.red
            )
            ConnectionStatusState.Connected -> TabState.Static(
                selectedIcon = R.drawable.ic_connected_filled,
                notSelectedIcon = R.drawable.ic_connected,
                textId = R.string.connection_status_connected,
                selectedColor = DesignSystem.color.accent_secondary,
                unselectedColor = DesignSystem.color.black_30,
                unselectedColorIcon = DesignSystem.color.accent_secondary
            )
            ConnectionStatusState.Synchronization -> TabState.Animated(
                selectedIcon = R.raw.ic_syncing_filled,
                notSelectedIcon = R.raw.ic_syncing,
                textId = R.string.connection_status_syncing,
                selectedColor = DesignSystem.color.accent_secondary,
                unselectedColor = DesignSystem.color.black_30,
                textDotsAnimated = true
            )
            is ConnectionStatusState.Synchronized -> TabState.Static(
                selectedIcon = R.drawable.ic_synced_filled,
                notSelectedIcon = R.drawable.ic_synced,
                textId = R.string.connection_status_synced,
                selectedColor = DesignSystem.color.accent_secondary,
                unselectedColor = DesignSystem.color.black_30,
                unselectedColorIcon = DesignSystem.color.accent_secondary
            )
        }
    }
}
