package com.flipperdevices.bridge.rpc.impl.api

import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.rpc.impl.delegates.MkDirDelegate
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@ContributesBinding(AppGraph::class, FlipperStorageApi::class)
class FlipperStorageApiImpl @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val mkDirDelegate: MkDirDelegate
) : FlipperStorageApi {
    override suspend fun mkdirs(path: String) {
        mkDirDelegate.mkdir(flipperServiceProvider.getServiceApi().requestApi, path)
    }

    override suspend fun delete(path: String, recursive: Boolean) {
        val requestApi = flipperServiceProvider.getServiceApi().requestApi
        val response = requestApi.request(
            main {
                storageDeleteRequest = deleteRequest {
                    this.path = path
                    this.recursive = recursive
                }
            }.wrapToRequest()
        ).first()
        if (response.commandStatus != Flipper.CommandStatus.OK &&
            response.commandStatus != Flipper.CommandStatus.ERROR_STORAGE_NOT_EXIST
        ) {
            error("Failed delete $path with error ${response.commandStatus}")
        }
    }
}
