package com.flipperdevices.updater.subghz.helpers

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface SkipProvisioningHelper {
    suspend fun shouldSkipProvisioning(
        fGetInfoFeatureApi: FGetInfoFeatureApi
    ): Boolean
}

@ContributesBinding(AppGraph::class, SkipProvisioningHelper::class)
class SkipProvisioningHelperImpl @Inject constructor(
    private val settings: DataStore<Settings>
) : SkipProvisioningHelper, LogTagProvider {
    override val TAG = "SkipProvisioningHelper"

    override suspend fun shouldSkipProvisioning(
        fGetInfoFeatureApi: FGetInfoFeatureApi
    ): Boolean {
        val ignoreSubGhzProvisioning = settings.data
            .first()
            .ignore_subghz_provisioning_on_zero_region
        info { "ignoreSubGhzProvisioning disabled, so continue subghz provisioning" }

        if (!ignoreSubGhzProvisioning) {
            return false
        }
        val hardwareRegion = fGetInfoFeatureApi
            .get(FGetInfoApiProperty.DeviceInfo.HARDWARE_REGION)
            .getOrNull()
            ?.toIntOrNull()

        if (hardwareRegion == null) {
            info { "Failed receive region" }
            return false
        }

        if (hardwareRegion != 0) {
            info { "Hardware region not zero, so return false" }
            return false
        }
        info { "Region hardware 0 and configuration enabled, skip subghz provisioning" }

        return true
    }
}
