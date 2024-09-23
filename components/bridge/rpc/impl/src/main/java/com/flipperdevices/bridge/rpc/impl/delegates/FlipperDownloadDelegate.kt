package com.flipperdevices.bridge.rpc.impl.delegates

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import com.flipperdevices.protobuf.storage.statRequest
import kotlinx.coroutines.flow.toList
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class FlipperDownloadDelegate @Inject constructor() : LogTagProvider {
    override val TAG = "FlipperDownloadDelegate"
    suspend fun download(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String,
        fileOnAndroid: File,
        externalProgressListener: ProgressListener
    ) {
        info { "Start download file $pathOnFlipper to ${fileOnAndroid.path}" }
        val progressListener = ProgressWrapperTracker(externalProgressListener)

        val totalSize = getTotalSize(requestApi, pathOnFlipper)
        info { "Receive total size of file: $totalSize bytes" }

        var totalDownloaded = 0L

        requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = pathOnFlipper
                }
            }.wrapToRequest()
        ).collect { response ->
            if (response.commandStatus == Flipper.CommandStatus.ERROR_STORAGE_NOT_EXIST) {
                throw FileNotFoundException()
            }
            if (response.commandStatus != Flipper.CommandStatus.OK) {
                error("Failed with $response")
            }
            val data = response.storageReadResponse.file.data
            fileOnAndroid.appendBytes(data.toByteArray())
            totalDownloaded += data.size()
            progressListener.onProgress(
                totalDownloaded,
                totalSize
            )
        }
    }

    private suspend fun getTotalSize(requestApi: FlipperRequestApi, pathOnFlipper: String): Long {
        val listingList = requestApi.request(
            main {
                storageStatRequest = statRequest {
                    path = pathOnFlipper
                }
            }.wrapToRequest()
        ).toList()
        if (listingList.isEmpty()) {
            error("Not received any response for listing $pathOnFlipper")
        }

        if (listingList.size > 1) {
            error(
                "Listing request return more than one response. " +
                    "Are you sure that you want download a file, not a directory?"
            )
        }

        val response = listingList.single()

        if (response.commandStatus == Flipper.CommandStatus.ERROR_STORAGE_NOT_EXIST) {
            throw FileNotFoundException()
        }

        if (!response.hasStorageStatResponse()) {
            error("Failed with $response")
        }

        return response.storageStatResponse.file.size.toLong()
    }
}
