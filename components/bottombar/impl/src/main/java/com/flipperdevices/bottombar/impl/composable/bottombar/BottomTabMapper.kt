package com.flipperdevices.bottombar.impl.composable.bottombar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.model.BottomBarNavigationConfig
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun getTabStateFromFlipperBottomTab(
    connectionTabState: TabState,
    bottomTab: BottomBarNavigationConfig,
    hubHasNotification: Boolean
): TabState {
    return when (bottomTab) {
        BottomBarNavigationConfig.Device -> connectionTabState
        BottomBarNavigationConfig.Archive -> TabState.Static(
            selectedIcon = R.drawable.ic_archive_selected,
            notSelectedIcon = R.drawable.ic_archive_unselected,
            text = stringResource(R.string.bar_title_archive),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected
        )

        BottomBarNavigationConfig.Hub -> TabState.Static(
            selectedIcon = R.drawable.ic_hub_filled,
            notSelectedIcon = R.drawable.ic_hub,
            text = stringResource(R.string.bar_title_hub),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected,
            notificationDotActive = hubHasNotification
        )
    }
}
