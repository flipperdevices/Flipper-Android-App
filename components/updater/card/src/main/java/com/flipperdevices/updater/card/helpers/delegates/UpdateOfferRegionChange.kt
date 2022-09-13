package com.flipperdevices.updater.card.helpers.delegates

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateOfferDelegate::class)
class UpdateOfferRegionChange @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper
) : UpdateOfferDelegate {

    private suspend fun isRegionChanged(lastProvidedRegion: String): Boolean {
        return try {
            // In this case, when the region is received, we can get an error.
            // When we receive new firmware we handle errors with the internet(and setup state),
            // so in this case we will return that the region has changed
            // to be sure to flash a new region if we got into a bad timing for obtaining a region
            val currentRegion = subGhzProvisioningHelper.getRegion()
            currentRegion != lastProvidedRegion
        } catch (@Suppress("SwallowedException") exception: Exception) {
            true
        }
    }

    override fun isRequire(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return dataStoreSettings.data.map { isRegionChanged(it.lastProvidedRegion) }
    }
}
