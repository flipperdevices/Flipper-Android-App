package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageNotExistException
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.NameWithHash
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.ListRequest
import com.flipperdevices.protobuf.storage.Md5sumRequest
import kotlinx.coroutines.flow.toList
import okio.Path.Companion.toPath

class FlipperListingDelegateNew(
    requestApi: FRpcFeatureApi
) : FlipperListingDelegate(requestApi) {
    override suspend fun listingWithMd5(
        pathOnFlipper: String
    ): List<NameWithHash> {
        return listingInternal(
            pathOnFlipper,
            includeMd5Flag = true
        ).mapNotNull {
            if (it.md5sum.isBlank()) {
                return@mapNotNull null
            }
            NameWithHash(
                it.name,
                it.md5sum,
                it.size,
                it.type.toInternalType()
            )
        }
    }
}