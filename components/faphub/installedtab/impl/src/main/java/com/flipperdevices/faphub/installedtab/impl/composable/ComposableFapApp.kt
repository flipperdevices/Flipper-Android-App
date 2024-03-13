package com.flipperdevices.faphub.installedtab.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installedtab.impl.composable.offline.ComposableOfflineFapApp
import com.flipperdevices.faphub.installedtab.impl.composable.online.ComposableOnlineFapApp
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.model.InstalledFapApp

@Composable
internal fun ComposableFapApp(
    installedFapApp: InstalledFapApp,
    state: FapInstalledInternalState,
    installationButton: @Composable (FapItemShort, Modifier) -> Unit,
    uninstallButtonOffline: @Composable (InstalledFapApp.OfflineFapApp, Modifier) -> Unit,
    uninstallButtonOnline: @Composable (FapItemShort, Modifier) -> Unit,
    onOpenFapItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (installedFapApp) {
        is InstalledFapApp.OfflineFapApp -> ComposableOfflineFapApp(
            offlineFapApp = installedFapApp,
            uninstallButton = {
                uninstallButtonOffline(installedFapApp, it)
            },
            modifier = modifier.clickable(
                onClick = { onOpenFapItem(installedFapApp.applicationUid) }
            )
        )

        is InstalledFapApp.OnlineFapApp -> ComposableOnlineFapApp(
            modifier = modifier
                .clickable(
                    onClick = { onOpenFapItem(installedFapApp.applicationUid) }
                ),
            fapItem = installedFapApp.fapItemShort,
            installationButton = {
                installationButton(installedFapApp.fapItemShort, it)
            },
            uninstallButton = {
                when (state) {
                    FapInstalledInternalState.Installed,
                    FapInstalledInternalState.InstalledOffline,
                    is FapInstalledInternalState.ReadyToUpdate ->
                        uninstallButtonOnline(installedFapApp.fapItemShort, it)

                    FapInstalledInternalState.InstallingInProgress,
                    FapInstalledInternalState.InstallingInProgressActive,
                    FapInstalledInternalState.UpdatingInProgress,
                    FapInstalledInternalState.UpdatingInProgressActive -> {
                    }
                }
            }
        )
    }
}
