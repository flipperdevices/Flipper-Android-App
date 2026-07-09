package com.flipperdevices.updater.card.helpers.delegates

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesIntoSet(AppGraph::class, binding<UpdateOfferDelegate>())
class UpdateOfferDebugFlagAlways @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>
) : UpdateOfferDelegate {

    override fun isRequire(fStorageFeatureApi: FStorageFeatureApi): Flow<Boolean> {
        return dataStoreSettings.data.map { it.always_update }
    }
}
