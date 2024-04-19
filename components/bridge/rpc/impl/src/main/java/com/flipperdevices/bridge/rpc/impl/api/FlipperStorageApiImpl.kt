package com.flipperdevices.bridge.rpc.impl.api

import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.rpc.api.model.NameWithHash
import com.flipperdevices.bridge.rpc.impl.delegates.FlipperDownloadDelegate
import com.flipperdevices.bridge.rpc.impl.delegates.FlipperListingDelegate
import com.flipperdevices.bridge.rpc.impl.delegates.FlipperUploadDelegate
import com.flipperdevices.bridge.rpc.impl.delegates.MkDirDelegate
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperStorageApi::class)
class FlipperStorageApiImpl @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val mkDirDelegate: MkDirDelegate,
    private val flipperUploadDelegate: FlipperUploadDelegate,
    private val listingDelegate: FlipperListingDelegate,
    private val flipperDownloadDelegate: FlipperDownloadDelegate
) : FlipperStorageApi, LogTagProvider {
    override val TAG = "FlipperStorageApi"

    override suspend fun mkdirs(path: String) = withContext(FlipperDispatchers.workStealingDispatcher) {
        mkDirDelegate.mkdir(flipperServiceProvider.getServiceApi().requestApi, path)
    }

    override suspend fun delete(path: String, recursive: Boolean) =
        withContext(FlipperDispatchers.workStealingDispatcher) {
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

    override suspend fun download(
        pathOnFlipper: String,
        fileOnAndroid: File,
        progressListener: ProgressListener
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        flipperDownloadDelegate.download(
            requestApi = flipperServiceProvider.getServiceApi().requestApi,
            pathOnFlipper = pathOnFlipper,
            fileOnAndroid = fileOnAndroid,
            externalProgressListener = progressListener
        )
    }

    override suspend fun upload(
        pathOnFlipper: String,
        fileOnAndroid: File,
        progressListener: ProgressListener
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        flipperUploadDelegate.upload(
            requestApi = flipperServiceProvider.getServiceApi().requestApi,
            pathOnFlipper = pathOnFlipper,
            fileOnAndroid = fileOnAndroid,
            externalProgressListener = progressListener
        )
    }

    override suspend fun listingDirectory(pathOnFlipper: String): List<String> {
        return listingDelegate.listing(
            requestApi = flipperServiceProvider.getServiceApi().requestApi,
            pathOnFlipper = pathOnFlipper
        )
    }

    override suspend fun listingDirectoryWithMd5(pathOnFlipper: String): List<NameWithHash> {
        val serviceApi = flipperServiceProvider.getServiceApi()
        val md5ListingSupported =
            serviceApi.flipperVersionApi.isSupported(Constants.API_SUPPORTED_MD5_LISTING)
        return if (md5ListingSupported) {
            info { "Use new md5 request api" }
            listingDelegate.listingWithMd5(
                requestApi = serviceApi.requestApi,
                pathOnFlipper = pathOnFlipper
            )
        } else {
            info { "Use deprecated md5 request api" }
            listingDelegate.listingWithMd5Deprecated(
                requestApi = serviceApi.requestApi,
                pathOnFlipper = pathOnFlipper
            )
        }
    }
}
