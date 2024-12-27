package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.protobuf.Region
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okio.Sink
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
                    fStorageFeatureApi.downloadApi().source(REGION_FILE, this)
                        .buffer()
                        .readByteArray()
                }
            }.onFailure {
                it.printStackTrace()
                error(it) { "#isRequire could not read region file!" }
                emit(true)
            }.onSuccess {
                val region = Region.ADAPTER.decode(it)
                val code = region.country_code.string(Charset.forName("ASCII"))
                val provisionedRegion = runCatching {
                    subGhzProvisioningHelper.getRegion()
                }.onFailure { it.printStackTrace() }.getOrNull()
                println("provisionedRegion: $provisionedRegion; code: $code")
                emit(provisionedRegion != code)
            }
        }
    }

    companion object {
        const val REGION_FILE = "/int/.region_data"
    }
}
