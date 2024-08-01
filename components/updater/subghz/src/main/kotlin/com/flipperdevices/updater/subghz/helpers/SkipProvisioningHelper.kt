package com.flipperdevices.updater.subghz.helpers

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface SkipProvisioningHelper {
    suspend fun shouldSkipProvisioning(
        serviceApi: FlipperServiceApi
    ): Boolean
}

private const val RPC_KEY_HARDWARE_REGION = "hardware.region.builtin"

@ContributesBinding(AppGraph::class, SkipProvisioningHelper::class)
class SkipProvisioningHelperImpl @Inject constructor(
    private val settings: DataStore<Settings>
) : SkipProvisioningHelper, LogTagProvider {
    override val TAG = "SkipProvisioningHelper"

    override suspend fun shouldSkipProvisioning(
        serviceApi: FlipperServiceApi
    ): Boolean {
        val ignoreSubGhzProvisioning = settings.data.first().ignore_subghz_provisioning_on_zero_region
        info { "ignoreSubGhzProvisioning disabled, so continue subghz provisioning" }

        if (!ignoreSubGhzProvisioning) {
            return false
        }
        info { "Try receive version" }
        if (!serviceApi.flipperVersionApi.isSupported(Constants.API_SUPPORTED_GET_REQUEST)) {
            info { "Version less then ${Constants.API_SUPPORTED_GET_REQUEST}, so return false" }
            return false
        }
        val hardwareRegion = getHardwareRegion(serviceApi.requestApi)

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

    private suspend fun getHardwareRegion(requestApi: FlipperRequestApi): Int? {
        val response = requestApi.request(
            flowOf(
                main {
                    propertyGetRequest = getRequest {
                        key = RPC_KEY_HARDWARE_REGION
                    }
                }.wrapToRequest()
            )
        )

        if (response.hasPropertyGetResponse().not()) {
            return null
        }

        return response.propertyGetResponse.value.toIntOrNull()
    }
}
