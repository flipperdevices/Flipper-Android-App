package com.flipperdevices.faphub.installedtab.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.flipperdevices.faphub.installedtab.api.FapInstalledApi
import com.flipperdevices.faphub.installedtab.impl.composable.ComposableInstalledTabScreen
import com.flipperdevices.faphub.installedtab.impl.model.FapBatchUpdateButtonState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsViewModel
import com.squareup.anvil.annotations.ContributesBinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstalledApi::class)
class FapInstalledApiImpl @Inject constructor(
    private val fapInstallationUIApi: FapInstallationUIApi
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
                FapBatchUpdateButtonState.UpdatingInProgress -> 0

                is FapBatchUpdateButtonState.ReadyToUpdate -> it.count
            }
        }
    }

    @Composable
    override fun ComposableInstalledTab(onOpenFapItem: (FapItemShort) -> Unit) {
        ComposableInstalledTabScreen(
            onOpenFapItem = onOpenFapItem,
            installationButton = { fapItem, modifier ->
                fapInstallationUIApi.ComposableButton(
                    config = fapItem?.toFapButtonConfig(),
                    modifier = modifier,
                    fapButtonSize = FapButtonSize.COMPACTED
                )
            }
        )
    }
}
