package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.ui.R as DesignSystem

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
            selectedIcon = R.drawable.ic_archive_selected,
            notSelectedIcon = R.drawable.ic_archive_unselected,
            textId = R.string.bar_title_archive,
            selectedColor = DesignSystem.color.black_80,
            unselectedColor = DesignSystem.color.black_30
        )
        FlipperBottomTab.OPTIONS -> TabState.Static(
            selectedIcon = R.drawable.ic_options_selected,
            notSelectedIcon = R.drawable.ic_options_unselected,
            textId = R.string.bar_title_options,
            selectedColor = DesignSystem.color.black_80,
            unselectedColor = DesignSystem.color.black_30
        )
    }
}
