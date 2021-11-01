package com.flipperdevices.info.impl.navigation

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.info.api.screen.InfoScreenProvider
import com.flipperdevices.info.impl.main.InfoFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class InfoScreenProviderImpl @Inject constructor() : InfoScreenProvider {
    override fun deviceInformationScreen() = FragmentScreen() { InfoFragment() }
}
