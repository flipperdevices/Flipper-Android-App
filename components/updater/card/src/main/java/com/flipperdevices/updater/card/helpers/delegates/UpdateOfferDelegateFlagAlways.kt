package com.flipperdevices.updater.card.helpers.delegates

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateOfferDelegate::class)
class UpdateOfferDelegateFlagAlways @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>
) : UpdateOfferDelegate {

    override fun isRequire(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return dataStoreSettings.data.map { it.alwaysUpdate }
    }
}
