package com.flipperdevices.hub.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.installedtab.api.FapUpdatePendingCountApi
import com.flipperdevices.hub.api.HubApi
import com.flipperdevices.nfc.attack.api.NfcAttackApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class)
class HubApiImpl @Inject constructor(
    private val nfcAttackApi: Provider<NfcAttackApi>,
    private val fapUpdatePendingCountApi: Provider<FapUpdatePendingCountApi>
) : HubApi {
    override fun hasNotification(scope: CoroutineScope): Flow<Boolean> {
        return combine(
            nfcAttackApi.get().notificationCount(),
            fapUpdatePendingCountApi.get().getUpdatePendingCount()
        ) { (nfcNotificationCount, appUpdates) ->
            nfcNotificationCount > 0 || appUpdates > 0
        }
    }
}
