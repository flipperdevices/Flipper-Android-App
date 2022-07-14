package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun getTabStateFromFlipperBottomTab(
    connectionApi: ConnectionApi,
    bottomTab: FlipperBottomTab
): TabState {
    connectionApi.CheckAndShowUnsupportedDialog()
    return when (bottomTab) {
        FlipperBottomTab.DEVICE -> {
            connectionApi.getConnectionTabState()
        }
        FlipperBottomTab.ARCHIVE -> TabState.Static(
            selectedIcon = R.drawable.ic_archive_selected,
            notSelectedIcon = R.drawable.ic_archive_unselected,
            text = stringResource(com.flipperdevices.bottombar.impl.R.string.bar_title_archive),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected
        )
        FlipperBottomTab.OPTIONS -> TabState.Static(
            selectedIcon = R.drawable.ic_options_selected,
            notSelectedIcon = R.drawable.ic_options_unselected,
            text = stringResource(com.flipperdevices.bottombar.impl.R.string.bar_title_options),
            selectedColor = LocalPallet.current.bottomBarSelected,
            unselectedColor = LocalPallet.current.bottomBarUnselected
        )
    }
}
