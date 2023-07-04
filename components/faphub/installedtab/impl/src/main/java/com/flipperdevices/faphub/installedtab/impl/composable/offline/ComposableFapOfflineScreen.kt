package com.flipperdevices.faphub.installedtab.impl.composable.offline

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.faphub.installedtab.impl.composable.common.ComposableLoadingItemDivider
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import kotlinx.collections.immutable.ImmutableList

@Suppress("FunctionNaming")
fun LazyListScope.ComposableFapOfflineScreen(
    offlineApps: ImmutableList<OfflineFapApp>,
    uninstallButton: @Composable (OfflineFapApp, Modifier) -> Unit,
    onOpen: (uid: String) -> Unit
) {
    items(
        count = offlineApps.size,
        key = { offlineApps[it].applicationUid }
    ) { index ->
        val fapApp = offlineApps[index]
        ComposableOfflineFapApp(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .clickableRipple { onOpen(fapApp.applicationUid) },
            offlineFapApp = fapApp,
            uninstallButton = { uninstallButton(fapApp, it) }
        )

        if (index != offlineApps.lastIndex) {
            ComposableLoadingItemDivider()
        }
    }
}
