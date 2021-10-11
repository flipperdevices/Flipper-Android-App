package com.flipperdevices.info.navigation

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.navigation.screen.InfoScreenProvider
import com.flipperdevices.core.utils.withArgs
import com.flipperdevices.info.main.InfoFragment
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
