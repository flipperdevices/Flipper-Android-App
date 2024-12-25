package com.flipperdevices.updater.impl.api

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGattInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.model.FirmwareVersion
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperVersionProviderApi::class)
class FlipperVersionProviderApiImpl @Inject constructor(
    private val firmwareVersionBuilderApi: FirmwareVersionBuilderApi,
    private val fFeatureProvider: FFeatureProvider
) : FlipperVersionProviderApi, LogTagProvider {
    override val TAG: String = "FlipperVersionProviderApiImpl"

    override fun getCurrentFlipperVersion(): Flow<FirmwareVersion?>  {
        return fFeatureProvider.get<FGattInfoFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FGattInfoFeatureApi> }
            .flatMapLatest { status -> status?.featureApi?.getGattInfoFlow() ?: flowOf(null) }
            .map { gattInfo -> gattInfo?.softwareVersion }
            .map { softwareVersion ->
                if (softwareVersion != null) {
                    firmwareVersionBuilderApi.buildFirmwareVersionFromString(softwareVersion)
                } else {
                    null
                }
            }
    }

}
