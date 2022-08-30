package com.flipperdevices.updater.card.helpers

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class UpdateOfferHelper @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val fileExistHelper: FileExistHelper,
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper
) {

    fun isAlwaysUpdate(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return combine(
            dataStoreSettings.data,
            isManifestExist(serviceApi),
            isSubGhzProvisioningExist(serviceApi)
        ) { setting, isManifestExist, isSubGhzProvisioningExist ->
            return@combine setting.alwaysUpdate || !isManifestExist ||
                !isSubGhzProvisioningExist || isRegionChanged(setting.lastProvidedRegion)
        }
    }

    private suspend fun isRegionChanged(lastProvidedRegion: String): Boolean {
        return try {
            // In this case, when the region is received, we can get an error.
            // When we receive new firmware we handle errors with the internet(and setup state),
            // so in this case we will return that the region has changed
            // to be sure to flash a new region if we got into a bad timing for obtaining a region
            val currentRegion = subGhzProvisioningHelper.getRegion()
            currentRegion != lastProvidedRegion
        } catch (exception: Exception) {
            true
        }
    }

    private fun isManifestExist(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return fileExistHelper.isFileExist(Constants.PATH.MANIFEST_FILE, serviceApi.requestApi)
    }

    private fun isSubGhzProvisioningExist(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return fileExistHelper.isFileExist(Constants.PATH.REGION_FILE, serviceApi.requestApi)
    }
}
