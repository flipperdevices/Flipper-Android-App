package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.update.api.RegionApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.protobuf.Region
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.buffer
import java.nio.charset.Charset
import javax.inject.Inject

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateOfferDelegate::class)
class UpdateOfferRegionChange @Inject constructor(
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper
) : UpdateOfferDelegate, LogTagProvider {
    override val TAG: String = "UpdateOfferRegionChange"

    override fun isRequire(fStorageFeatureApi: FStorageFeatureApi): Flow<Boolean> {
        return flow {
            runCatching {
                coroutineScope {
                    fStorageFeatureApi.downloadApi().source(RegionApi.REGION_FILE, this)
                        .buffer()
                        .readByteArray()
                }
            }.onFailure {
                error(it) { "#isRequire could not read region file!" }
                emit(true)
            }.onSuccess {
                val region = Region.ADAPTER.decode(it)
                val code = region.country_code.string(Charset.forName("ASCII"))
                val provisionedRegion = runCatching {
                    subGhzProvisioningHelper.getRegion()
                }.getOrNull()
                emit(provisionedRegion != code)
            }
        }
    }
}
