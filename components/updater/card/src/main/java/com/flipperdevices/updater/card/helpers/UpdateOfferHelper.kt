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
            val isRegionChanged = subGhzProvisioningHelper.getRegion() != setting.lastProvidedRegion
            return@combine setting.alwaysUpdate || !isManifestExist ||
                !isSubGhzProvisioningExist || isRegionChanged
        }
    }

    private fun isManifestExist(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return fileExistHelper.isFileExist(Constants.PATH.MANIFEST_FILE, serviceApi.requestApi)
    }

    private fun isSubGhzProvisioningExist(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return fileExistHelper.isFileExist(Constants.PATH.REGION_FILE, serviceApi.requestApi)
    }
}
