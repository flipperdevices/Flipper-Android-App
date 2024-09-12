package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageNotExistException
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.NameWithHash
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.ListRequest
import kotlinx.coroutines.flow.toList

const val SIZE_BYTES_LIMIT = 10 * 1024 * 1024 // 10MiB

abstract class FlipperListingDelegate(
    val requestApi: FRpcFeatureApi
) : LogTagProvider {
    override val TAG = "FlipperListingDelegate"

    suspend fun listing(pathOnFlipper: String): List<String> {
        return listingInternal(pathOnFlipper, includeMd5Flag = false).map { it.name }
    }

    suspend fun listingInternal(
        pathOnFlipper: String,
        includeMd5Flag: Boolean
    ): List<File> {
        return requestApi.request(
            Main(
                storage_list_request = ListRequest(
                    path = pathOnFlipper,
                    include_md5 = includeMd5Flag,
                    filter_max_size = SIZE_BYTES_LIMIT
                )
            ).wrapToRequest()
        ).toList().map { response ->
            val exception = response.exceptionOrNull()
            if (exception != null) {
                return@map if (exception is FRpcStorageNotExistException) {
                    info { "Listing request for $pathOnFlipper with $response was not found" }
                    listOf()
                } else {
                    throw exception
                }
            }

            val listResponse = response.getOrNull()?.storage_list_response
                ?: error("Can't find storage list response, $response")
            return@map listResponse.file_
        }.flatten()
    }

    abstract suspend fun listingWithMd5(
        pathOnFlipper: String
    ): List<NameWithHash>
}
