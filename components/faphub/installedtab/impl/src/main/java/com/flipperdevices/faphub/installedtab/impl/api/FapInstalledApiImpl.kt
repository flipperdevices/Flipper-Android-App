package com.flipperdevices.faphub.installedtab.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.flipperdevices.faphub.installedtab.api.FapInstalledApi
import com.flipperdevices.faphub.installedtab.impl.composable.ComposableInstalledTabScreen
import com.flipperdevices.faphub.installedtab.impl.composable.offline.dialog.ComposableOfflineAppDialogBox
import com.flipperdevices.faphub.installedtab.impl.model.FapBatchUpdateButtonState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsViewModel
import com.flipperdevices.faphub.uninstallbutton.api.FapUninstallApi
import com.squareup.anvil.annotations.ContributesBinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstalledApi::class)
class FapInstalledApiImpl @Inject constructor(
    private val fapInstallationUIApi: FapInstallationUIApi,
    private val uninstallApi: FapUninstallApi
) : FapInstalledApi {

    @Composable
    override fun getUpdatePendingCount(): Int {
        val installedViewModel = tangleViewModel<InstalledFapsViewModel>()
        val buttonStateFlow = remember { installedViewModel.getFapBatchUpdateButtonState() }
        val state by buttonStateFlow.collectAsState()
        return state.let {
            when (it) {
                FapBatchUpdateButtonState.Loading,
                FapBatchUpdateButtonState.NoUpdates,
                FapBatchUpdateButtonState.UpdatingInProgress,
                FapBatchUpdateButtonState.Offline -> 0

                is FapBatchUpdateButtonState.ReadyToUpdate -> it.count
            }
        }
    }

    @Composable
    override fun ComposableInstalledTab(onOpenFapItem: (uid: String) -> Unit) {
        ComposableInstalledTabScreen(
            onOpenFapItem = onOpenFapItem,
            installationButton = { fapItem, modifier ->
                fapInstallationUIApi.ComposableButton(
                    config = fapItem?.toFapButtonConfig(),
                    modifier = modifier,
                    fapButtonSize = FapButtonSize.COMPACTED,
                )
            },
            uninstallButtonOffline = { offlineFapApp, modifier ->
                uninstallApi.ComposableFapUninstallButton(
                    modifier = modifier,
                    applicationUid = offlineFapApp.applicationUid
                ) {
                    ComposableOfflineAppDialogBox(offlineFapApp = offlineFapApp, modifier = it)
                }
            },
            uninstallButtonOnline = { fapItemShort, modifier ->
                uninstallApi.ComposableFapUninstallButton(
                    modifier = modifier,
                    fapItem = fapItemShort
                )
            }
        )
    }
}
