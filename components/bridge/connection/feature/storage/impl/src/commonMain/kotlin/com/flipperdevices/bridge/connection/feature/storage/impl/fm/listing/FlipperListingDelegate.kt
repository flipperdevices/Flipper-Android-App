package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItemWithHash
import com.flipperdevices.core.ktx.jre.mapCatching
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.ListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val SIZE_BYTES_LIMIT = 10 * 1024 * 1024 // 10MiB

abstract class FlipperListingDelegate(
    protected val requestApi: FRpcFeatureApi
) : LogTagProvider {
    override val TAG = "FlipperListingDelegate"

    suspend fun listing(pathOnFlipper: String): Flow<Result<List<ListingItem>>> {
        return listingInternal(pathOnFlipper, includeMd5Flag = false).mapCatching { list ->
            list.map { item ->
                ListingItem(
                    fileName = item.name,
                    fileType = item.type.toInternalType(),
                    size = item.size.toLong()
                )
            }
        }
    }

    protected suspend fun listingInternal(
        pathOnFlipper: String,
        includeMd5Flag: Boolean
    ): Flow<Result<List<File>>> {
        return requestApi.request(
            Main(
                storage_list_request = ListRequest(
                    path = pathOnFlipper,
                    include_md5 = includeMd5Flag,
                    filter_max_size = SIZE_BYTES_LIMIT
                )
            ).wrapToRequest()
        ).mapCatching { response ->
            val listResponse = response.storage_list_response
                ?: error("Can't find storage list response, $response")
            return@mapCatching listResponse.file_
        }
    }

    abstract suspend fun listingWithMd5(
        pathOnFlipper: String
    ): Flow<Result<List<ListingItemWithHash>>>
}
