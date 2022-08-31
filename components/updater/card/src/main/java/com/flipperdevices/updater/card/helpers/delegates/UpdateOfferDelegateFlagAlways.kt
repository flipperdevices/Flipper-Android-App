package com.flipperdevices.updater.card.helpers.delegates

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.updater.card.di.CardComponent
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UpdateOfferDelegateFlagAlways @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>
) : UpdateOfferDelegate {

    init {
        ComponentHolder.component<CardComponent>().inject(this)
    }

    override fun isRequire(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return dataStoreSettings.data.map { it.alwaysUpdate }
    }
}
