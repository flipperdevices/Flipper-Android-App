package com.flipperdevices.bridge.connection.feature.storage.impl.fm

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.FFileStorageApi
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.Md5sumRequest

class FFileStorageMD5ApiImpl(
    private val rpcApi: FRpcFeatureApi
) : FFileStorageApi {
    override suspend fun md5(path: String): Result<String> = runCatching {
        val response = rpcApi.requestOnce(
            Main(
                storage_md5sum_request = Md5sumRequest(path = path)
            ).wrapToRequest()
        )
        response.command_status
    }
}