package com.flipperdevices.hub.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.hub.api.HubApi
import com.flipperdevices.hub.impl.fragments.HubFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class HubApiImpl @Inject constructor() : HubApi {
    override fun getHubScreen(): Screen {
        return FragmentScreen { HubFragment() }
    }
}
