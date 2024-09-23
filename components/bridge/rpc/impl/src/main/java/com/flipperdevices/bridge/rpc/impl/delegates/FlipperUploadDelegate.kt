package com.flipperdevices.bridge.rpc.impl.delegates

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class FlipperUploadDelegate @Inject constructor() : LogTagProvider {
    override val TAG = "FlipperUploadDelegate"
    suspend fun upload(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String,
        fileOnAndroid: File,
        externalProgressListener: ProgressListener
    ) {
        val progressListener = ProgressWrapperTracker(externalProgressListener)

        var currentProgress = 0L
        val lenght = fileOnAndroid.length()

        fileOnAndroid.inputStream().use { inputStream ->
            val requestFlow = streamToCommandFlow(
                inputStream,
                fileOnAndroid.length()
            ) { chunkData ->
                storageWriteRequest = writeRequest {
                    path = pathOnFlipper
                    file = file { data = chunkData }
                }
            }.map {
                FlipperRequest(
                    data = it,
                    onSendCallback = {
                        currentProgress += it.storageWriteRequest.file.data.size()
                        progressListener.onProgress(currentProgress, lenght)
                    }
                )
            }
            val response = requestApi.request(requestFlow)
            if (response.commandStatus != Flipper.CommandStatus.OK) {
                error("Failed with $response")
            }
        }
    }
}
