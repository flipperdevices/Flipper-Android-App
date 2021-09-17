package com.flipper.info.navigation

import com.flipper.core.di.AppGraph
import com.flipper.core.navigation.screen.InfoScreenProvider
import com.flipper.core.utils.withArgs
import com.flipper.info.main.InfoFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class InfoScreenProviderImpl @Inject constructor() : InfoScreenProvider {
    override fun deviceInformationScreen(deviceId: String) =
        FragmentScreen("Info_$deviceId") {
            InfoFragment().withArgs {
                putString(InfoFragment.EXTRA_DEVICE_KEY, deviceId)
            }
        }
}
