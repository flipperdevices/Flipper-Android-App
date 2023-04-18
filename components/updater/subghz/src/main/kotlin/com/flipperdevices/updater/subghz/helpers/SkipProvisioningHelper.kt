package com.flipperdevices.updater.subghz.helpers

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface SkipProvisioningHelper {
    suspend fun shouldSkipProvisioning(
        serviceApi: FlipperServiceApi
    ): Boolean
}

@ContributesBinding(AppGraph::class, SkipProvisioningHelper::class)
class SkipProvisioningHelperImpl @Inject constructor(
) : SkipProvisioningHelper, LogTagProvider {
    override val TAG = "SkipProvisioningHelper"

    override suspend fun shouldSkipProvisioning(
        serviceApi: FlipperServiceApi
    ): Boolean {
        info { "skipping subghz provisioning" }

        return true
    }
}
