package com.flipperdevices.bottombar.impl.composable.bottombar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.model.BottomBarTabEnum
import com.flipperdevices.bottombar.model.NotificationDot
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun getTabStateFromFlipperBottomTab(
    connectionTabState: TabState,
    bottomTab: BottomBarTabEnum,
    appsHasNotification: Boolean,
    toolsHasNotification: Boolean
): TabState {
    return when (bottomTab) {
        BottomBarTabEnum.DEVICE -> connectionTabState
        BottomBarTabEnum.ARCHIVE -> TabState.Static(
            selectedIcon = R.drawable.ic_archive_filled,
            notSelectedIcon = R.drawable.ic_archive,
            text = stringResource(R.string.bar_title_archive),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected
        )

        BottomBarTabEnum.APPS -> TabState.Static(
            selectedIcon = R.drawable.ic_tab_apps_filled,
            notSelectedIcon = R.drawable.ic_tab_apps,
            text = stringResource(R.string.bar_title_apps),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected,
            notificationDot = if (appsHasNotification) {
                NotificationDot(notificationDotPaddingEnd = 7.dp)
            } else {
                null
            }
        )

        BottomBarTabEnum.TOOLS -> TabState.Static(
            selectedIcon = R.drawable.ic_tools_filled,
            notSelectedIcon = R.drawable.ic_tools,
            text = stringResource(R.string.bar_title_tools),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected,
            notificationDot = if (toolsHasNotification) {
                NotificationDot(notificationDotPaddingEnd = 1.dp)
            } else {
                null
            }
        )
    }
}
