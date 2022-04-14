package com.flipperdevices.updater.impl.api

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.map
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.impl.utils.FirmwareVersionBuildHelper
import com.flipperdevices.updater.model.FirmwareVersion
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

@ContributesBinding(AppGraph::class)
class FlipperVersionProviderApiImpl @Inject constructor() : FlipperVersionProviderApi {
    override fun getCurrentFlipperVersion(
        coroutineScope: CoroutineScope,
        serviceApi: FlipperServiceApi
    ): StateFlow<FirmwareVersion?> {
        return serviceApi.flipperInformationApi.getInformationFlow().map(coroutineScope) {
            val softwareVersion = it.softwareVersion
            return@map if (softwareVersion != null) {
                FirmwareVersionBuildHelper.buildFirmwareVersionFromString(softwareVersion)
            } else null
        }
    }
}
