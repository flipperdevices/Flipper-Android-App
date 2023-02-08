package com.flipperdevices.hub.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.hub.api.HubApi
import com.flipperdevices.nfc.attack.api.NfcAttackApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class HubApiImpl @Inject constructor(
    private val nfcAttackApi: NfcAttackApi
) : HubApi {
    override fun hasNotification() = nfcAttackApi.notificationCount().map {
        it > 0
    }
}
