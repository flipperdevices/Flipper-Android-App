package com.flipperdevices.hub.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.hub.api.HubApi
import com.flipperdevices.hub.impl.fragments.HubFragment
import com.flipperdevices.nfc.attack.api.NfcAttackApi
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@ContributesBinding(AppGraph::class)
class HubApiImpl @Inject constructor(
    private val nfcAttackApi: NfcAttackApi
) : HubApi {
    override fun hasNotification() = nfcAttackApi.notificationCount().map {
        it > 0
    }

    override fun getHubScreen(): Screen {
        return FragmentScreen { HubFragment() }
    }
}
