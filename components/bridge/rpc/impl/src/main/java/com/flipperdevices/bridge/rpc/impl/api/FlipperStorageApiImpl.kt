package com.flipperdevices.bridge.rpc.impl.api

import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.rpc.impl.delegates.FlipperUploadDelegate
import com.flipperdevices.bridge.rpc.impl.delegates.MkDirDelegate
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperStorageApi::class)
class FlipperStorageApiImpl @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val mkDirDelegate: MkDirDelegate,
    private val flipperUploadDelegate: FlipperUploadDelegate
) : FlipperStorageApi {
    override suspend fun mkdirs(path: String) = withContext(Dispatchers.Default) {
        mkDirDelegate.mkdir(flipperServiceProvider.getServiceApi().requestApi, path)
    }

    override suspend fun delete(path: String, recursive: Boolean) = withContext(Dispatchers.Default) {
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

    override suspend fun upload(
        pathOnFlipper: String,
        fileOnAndroid: File,
        progressListener: ProgressListener
    ) = withContext(Dispatchers.Default) {
        flipperUploadDelegate.upload(
            requestApi = flipperServiceProvider.getServiceApi().requestApi,
            pathOnFlipper = pathOnFlipper,
            fileOnAndroid = fileOnAndroid,
            externalProgressListener = progressListener
        )
    }
}
