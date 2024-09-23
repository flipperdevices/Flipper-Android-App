package com.flipperdevices.faphub.installation.queue.impl.executor.actions

import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.utils.FapHubTmpFolderProvider
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

class FapActionUpload @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val tmpFolderProvider: FapHubTmpFolderProvider
) : LogTagProvider {
    override val TAG = "FapActionUpload"

    suspend fun upload(
        fapFile: File,
        progressListener: ProgressListener
    ): String {
        info { "Start upload ${fapFile.absolutePath}" }
        val requestApi = serviceProvider.getServiceApi().requestApi
        val fapPath = File(
            tmpFolderProvider.provideTmpFolder(),
            "tmp.fap"
        ).absolutePath
        val progressWrapper = ProgressWrapperTracker(progressListener)
        val totalLength = fapFile.length()
        var uploadedBytes = 0L
        val response = fapFile.inputStream().use { inputStream ->
            val requestFlow = streamToCommandFlow(inputStream, totalLength) { chunkData ->
                storageWriteRequest = writeRequest {
                    path = fapPath
                    file = file { data = chunkData }
                }
            }.map {
                FlipperRequest(
                    data = it,
                    onSendCallback = {
                        uploadedBytes += it.storageWriteRequest.file.data.size()
                        runBlocking {
                            progressWrapper.onProgress(uploadedBytes, totalLength)
                        }
                    }
                )
            }

            requestApi.request(requestFlow)
        }

        if (response.commandStatus != Flipper.CommandStatus.OK) {
            error("Failed upload tmp manifest, command status is ${response.commandStatus}")
        }
        return fapPath
    }
}
