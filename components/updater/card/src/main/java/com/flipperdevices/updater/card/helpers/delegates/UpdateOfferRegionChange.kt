package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.Flipper.Region
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import com.squareup.anvil.annotations.ContributesMultibinding
import java.nio.charset.Charset
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateOfferDelegate::class)
class UpdateOfferRegionChange @Inject constructor(
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper
) : UpdateOfferDelegate {

    override fun isRequire(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return serviceApi.requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = Constants.PATH.REGION_FILE
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).map { response ->
            if (response.commandStatus != Flipper.CommandStatus.OK) return@map true
            if (!response.hasStorageReadResponse()) return@map true

            try {
                val array = response.storageReadResponse.file.data
                val region = Region.parseFrom(array)
                val code = region.countryCode.toString(Charset.forName("ASCII"))

                return@map subGhzProvisioningHelper.getRegion() != code
            } catch (@Suppress("SwallowedException") exception: Exception) {
                return@map true
            }
        }
    }
}
