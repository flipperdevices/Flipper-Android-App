package com.flipperdevices.updater.screen.api

import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.screen.fragments.UpdaterDialogBuilder
import com.flipperdevices.updater.screen.fragments.UpdaterFragment
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

    override fun openUpdateScreen(silent: Boolean, updateRequest: UpdateRequest?) {
        if (silent) {
            globalCicerone.getRouter().newRootScreen(
                FragmentScreen {
                    UpdaterFragment.getInstance(updateRequest)
                }
            )
        } else updaterDialogBuilder.showDialog(updateRequest)
    }
}
