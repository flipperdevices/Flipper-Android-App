package com.flipperdevices.info.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.info.api.screen.InfoScreenProvider
import com.flipperdevices.info.impl.fragment.InfoFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class InfoScreenProviderImpl @Inject constructor() : InfoScreenProvider {
    override fun deviceInformationScreen(deeplink: Deeplink?): Screen {
        return FragmentScreen { InfoFragment.newInstance(deeplink) }
    }
}
