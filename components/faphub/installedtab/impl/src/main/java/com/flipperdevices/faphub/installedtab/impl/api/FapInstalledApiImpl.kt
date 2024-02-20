package com.flipperdevices.faphub.installedtab.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
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
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, FapInstalledApi::class)
class FapInstalledApiImpl @Inject constructor(
    private val fapInstallationUIApi: FapInstallationUIApi,
    private val uninstallApi: FapUninstallApi,
    private val errorsRenderer: FapHubComposableErrorsRenderer,
    private val installedFapsViewModelProvider: Provider<InstalledFapsViewModel>
) : FapInstalledApi {

    @Composable
    override fun getUpdatePendingCount(componentContext: ComponentContext): Int {
        val installedViewModel = componentContext.viewModelWithFactory(key = null) {
            installedFapsViewModelProvider.get()
        }
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
    @Suppress("NonSkippableComposable")
    override fun ComposableInstalledTab(
        componentContext: ComponentContext,
        onOpenFapItem: (uid: String) -> Unit
    ) {
        val installedViewModel = componentContext.viewModelWithFactory(key = null) {
            installedFapsViewModelProvider.get()
        }
        ComposableInstalledTabScreen(
            onOpenFapItem = onOpenFapItem,
            installationButton = { fapItem, modifier ->
                fapInstallationUIApi.ComposableButton(
                    config = fapItem?.toFapButtonConfig(),
                    modifier = modifier,
                    fapButtonSize = FapButtonSize.COMPACTED,
                    componentContext = componentContext
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
            },
            errorsRenderer = errorsRenderer,
            viewModel = installedViewModel
        )
    }
}
