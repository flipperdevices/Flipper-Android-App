package com.flipperdevices.updater.ui.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.updater.api.UpdateCardApi
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.VersionFiles
import com.flipperdevices.updater.ui.fragments.UpdaterDialogBuilder
import com.flipperdevices.updater.ui.fragments.UpdaterFragment
import com.flipperdevices.updater.ui.viewmodel.UpdateCardViewModel
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@ContributesBinding(AppGraph::class)
class UpdaterUIApiImpl @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val globalCicerone: CiceroneGlobal
) : UpdaterUIApi {
    private val updaterDialogBuilder = UpdaterDialogBuilder(globalCicerone)

    @Composable
    override fun getUpdateCardApi(): UpdateCardApi {
        return viewModel<UpdateCardViewModel>()
    }

    @Composable
    override fun isUpdaterAvailable(): State<Boolean> {
        return dataStoreSettings.data.map { it.enabledUpdater }
            .collectAsState(false)
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
