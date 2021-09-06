package com.flipper.info.navigation

import com.flipper.core.di.AppGraph
import com.flipper.core.models.BLEDevice
import com.flipper.core.navigation.screen.InfoScreenProvider
import com.flipper.core.utils.withArgs
import com.flipper.info.main.InfoFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class InfoScreenProviderImpl @Inject constructor() : InfoScreenProvider {
    override fun deviceInformationScreen(device: BLEDevice) =
        FragmentScreen("Info_${device.id}") {
            InfoFragment().withArgs {
                putParcelable(InfoFragment.EXTRA_DEVICE_KEY, device)
            }
        }
}
