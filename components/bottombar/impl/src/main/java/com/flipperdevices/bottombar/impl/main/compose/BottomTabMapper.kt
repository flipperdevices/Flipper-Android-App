package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun getTabStateFromFlipperBottomTab(
    connectionApi: ConnectionApi,
    bottomTab: FlipperBottomTab
): TabState {
    return when (bottomTab) {
        FlipperBottomTab.DEVICE -> {
            val tabState by connectionApi.getConnectionTabState().collectAsState()
            return tabState
        }
        FlipperBottomTab.ARCHIVE -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_archive_selected,
            notSelectedIcon = DesignSystem.drawable.ic_archive_unselected,
            text = stringResource(R.string.bar_title_archive),
            selectedColor = LocalPallet.current.selectedBottomBar,
            unselectedColor = LocalPallet.current.unselectedBottomBar
        )
        FlipperBottomTab.OPTIONS -> TabState.Static(
            selectedIcon = DesignSystem.drawable.ic_options_selected,
            notSelectedIcon = DesignSystem.drawable.ic_options_unselected,
            text = stringResource(R.string.bar_title_options),
            selectedColor = LocalPallet.current.selectedBottomBar,
            unselectedColor = LocalPallet.current.unselectedBottomBar
        )
    }
}
