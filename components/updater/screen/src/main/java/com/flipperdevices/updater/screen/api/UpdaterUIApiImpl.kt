package com.flipperdevices.updater.screen.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.screen.fragments.UpdaterFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class UpdaterUIApiImpl @Inject constructor(
    private val globalCicerone: CiceroneGlobal
) : UpdaterUIApi {

    override fun openUpdateScreen(updateRequest: UpdateRequest?) {
        globalCicerone.getRouter().newRootScreen(
            FragmentScreen {
                UpdaterFragment.getInstance(updateRequest)
            }
        )
    }
}
