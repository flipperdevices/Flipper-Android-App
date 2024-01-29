package com.flipperdevices.updater.impl.api

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.model.FirmwareVersion
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class FlipperVersionProviderApiImpl @Inject constructor(
    private val firmwareVersionBuilderApi: FirmwareVersionBuilderApi
) : FlipperVersionProviderApi {
    override fun getCurrentFlipperVersion(
        coroutineScope: CoroutineScope,
        serviceApi: FlipperServiceApi
    ): Flow<FirmwareVersion?> {
        return serviceApi.flipperInformationApi.getInformationFlow().map {
            val softwareVersion = it.softwareVersion
            return@map if (softwareVersion != null) {
                firmwareVersionBuilderApi.buildFirmwareVersionFromString(softwareVersion)
            } else {
                null
            }
        }
    }
}
