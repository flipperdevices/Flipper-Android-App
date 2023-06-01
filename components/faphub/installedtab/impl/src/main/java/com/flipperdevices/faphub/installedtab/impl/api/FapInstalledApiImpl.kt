package com.flipperdevices.faphub.installedtab.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.flipperdevices.faphub.installedtab.api.FapInstalledApi
import com.flipperdevices.faphub.installedtab.impl.composable.ComposableInstalledTabScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstalledApi::class)
class FapInstalledApiImpl @Inject constructor(
    private val fapInstallationUIApi: FapInstallationUIApi
) : FapInstalledApi {
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
