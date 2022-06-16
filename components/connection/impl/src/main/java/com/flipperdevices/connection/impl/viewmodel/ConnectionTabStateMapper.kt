package com.flipperdevices.connection.impl.viewmodel

import android.content.Context
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.res.R as DesignSystem

object ConnectionTabStateMapper {
    @Suppress("LongMethod")
    fun getConnectionTabState(context: Context, connectionState: ConnectionStatusState): TabState {
        return when (connectionState) {
            ConnectionStatusState.NoDevice -> TabState.Static(
                selectedIcon = R.drawable.ic_no_device_filled,
                notSelectedIcon = R.drawable.ic_no_device,
                text = context.getString(R.string.connection_status_no_device),
                selectedColor = DesignSystem.color.black_40,
                unselectedColor = DesignSystem.color.black_30
            )
            ConnectionStatusState.Disconnected -> TabState.Static(
                selectedIcon = R.drawable.ic_disconnected_filled,
                notSelectedIcon = R.drawable.ic_disconnected,
                text = context.getString(R.string.connection_status_not_connected),
                selectedColor = DesignSystem.color.black_40,
                unselectedColor = DesignSystem.color.black_30
            )
            ConnectionStatusState.Connecting -> TabState.Animated(
                selectedIcon = R.raw.ic_connecting_filled,
                notSelectedIcon = R.raw.ic_connecting,
                text = context.getString(R.string.connection_status_connecting),
                selectedColor = DesignSystem.color.black_40,
                unselectedColor = DesignSystem.color.black_30,
                textDotsAnimated = true
            )
            ConnectionStatusState.Unsupported -> TabState.Static(
                selectedIcon = R.drawable.ic_no_device_filled,
                notSelectedIcon = R.drawable.ic_no_device,
                text = context.getString(R.string.connection_status_unsupported),
                selectedColor = DesignSystem.color.red,
                unselectedColor = DesignSystem.color.black_30,
                unselectedColorIcon = DesignSystem.color.red
            )
            ConnectionStatusState.Connected -> TabState.Static(
                selectedIcon = R.drawable.ic_connected_filled,
                notSelectedIcon = R.drawable.ic_connected,
                text = context.getString(R.string.connection_status_connected),
                selectedColor = DesignSystem.color.accent_secondary,
                unselectedColor = DesignSystem.color.black_30,
                unselectedColorIcon = DesignSystem.color.accent_secondary
            )
            is ConnectionStatusState.Synchronization -> TabState.Animated(
                selectedIcon = R.raw.ic_syncing_filled,
                notSelectedIcon = R.raw.ic_syncing,
                text = context.getString(
                    R.string.connection_status_syncing,
                    connectionState.progress.roundPercentToString()
                ),
                selectedColor = DesignSystem.color.accent_secondary,
                unselectedColor = DesignSystem.color.black_30
            )
            is ConnectionStatusState.Synchronized -> TabState.Static(
                selectedIcon = R.drawable.ic_synced_filled,
                notSelectedIcon = R.drawable.ic_synced,
                text = context.getString(R.string.connection_status_synced),
                selectedColor = DesignSystem.color.accent_secondary,
                unselectedColor = DesignSystem.color.black_30,
                unselectedColorIcon = DesignSystem.color.accent_secondary
            )
        }
    }
}
