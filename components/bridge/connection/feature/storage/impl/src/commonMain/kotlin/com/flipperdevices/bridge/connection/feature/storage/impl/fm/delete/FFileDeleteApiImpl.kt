package com.flipperdevices.bridge.connection.feature.storage.impl.fm.delete

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDeleteApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.connection.feature.storage.impl.utils.toRpc
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.DeleteRequest

class FFileDeleteApiImpl(
    private val rpcApi: FRpcFeatureApi
) : FFileDeleteApi {
    override suspend fun delete(
        path: String,
        recursive: Boolean,
        priority: StorageRequestPriority
    ): Result<Unit> {
        return rpcApi.requestOnce(
            Main(
                storage_delete_request = DeleteRequest(
                    path = path,
                    recursive = recursive
                )
            ).wrapToRequest(priority.toRpc())
        ).map { }
    }
}
