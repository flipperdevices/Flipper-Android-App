package com.flipperdevices.toolstab.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfc.attack.api.NfcAttackApi
import com.flipperdevices.toolstab.api.ToolsApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class)
class ToolsApiImpl @Inject constructor(
    private val nfcAttackApi: Provider<NfcAttackApi>
) : ToolsApi {
    override fun hasNotification(scope: CoroutineScope): Flow<Boolean> {
        return nfcAttackApi.get().notificationCount().map { nfcNotificationCount ->
            nfcNotificationCount > 0
        }
    }
}
