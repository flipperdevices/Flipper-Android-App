package com.flipperdevices.bottombar.impl.composable.bottombar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun getTabStateFromFlipperBottomTab(
    connectionApi: ConnectionApi,
    bottomTab: FlipperBottomTab,
    hubHasNotification: Boolean
): TabState {
    return when (bottomTab) {
        FlipperBottomTab.DEVICE -> {
            connectionApi.getConnectionTabState()
        }
        FlipperBottomTab.ARCHIVE -> TabState.Static(
            selectedIcon = R.drawable.ic_archive_selected,
            notSelectedIcon = R.drawable.ic_archive_unselected,
            text = stringResource(R.string.bar_title_archive),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected
        )
        FlipperBottomTab.HUB -> TabState.Static(
            selectedIcon = R.drawable.ic_hub_filled,
            notSelectedIcon = R.drawable.ic_hub,
            text = stringResource(R.string.bar_title_hub),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected,
            notificationDotActive = hubHasNotification
        )
    }
}
