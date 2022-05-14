package com.flipperdevices.updater.ui.api

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.updater.api.UpdateCardApi
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.VersionFiles
import com.flipperdevices.updater.ui.fragments.UpdaterDialogBuilder
import com.flipperdevices.updater.ui.fragments.UpdaterFragment
import com.flipperdevices.updater.ui.viewmodel.UpdateCardViewModel
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class UpdaterUIApiImpl @Inject constructor(
    private val globalCicerone: CiceroneGlobal,
    synchronizationApi: SynchronizationApi
) : UpdaterUIApi {
    private val updaterDialogBuilder = UpdaterDialogBuilder(
        globalCicerone,
        synchronizationApi
    )

    @Composable
    override fun getUpdateCardApi(): UpdateCardApi {
        return viewModel<UpdateCardViewModel>()
    }

    override fun openUpdateScreen(silent: Boolean, versionFiles: VersionFiles?) {
        if (silent) {
            globalCicerone.getRouter().newRootScreen(
                FragmentScreen {
                    UpdaterFragment.getInstance(versionFiles)
                }
            )
        } else updaterDialogBuilder.showDialog(versionFiles)
    }
}
