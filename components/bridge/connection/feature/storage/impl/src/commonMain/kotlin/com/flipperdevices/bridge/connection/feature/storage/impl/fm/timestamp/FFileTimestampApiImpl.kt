package com.flipperdevices.bridge.connection.feature.storage.impl.fm.timestamp

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileTimestampApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.connection.feature.storage.impl.utils.toRpc
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.TimestampRequest

class FFileTimestampApiImpl(private val rpcApi: FRpcFeatureApi) : FFileTimestampApi {
    override suspend fun fetchFolderTimestamp(
        folder: String,
        priority: StorageRequestPriority
    ): Long? {
        val response = rpcApi.requestOnce(
            Main(
                storage_timestamp_request = TimestampRequest(
                    path = folder
                )
            ).wrapToRequest(priority.toRpc())
        )
        return response.getOrNull()?.storage_timestamp_response?.timestamp?.toLong()
    }
}
