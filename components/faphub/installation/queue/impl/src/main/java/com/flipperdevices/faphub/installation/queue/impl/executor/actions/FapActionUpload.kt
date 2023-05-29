package com.flipperdevices.faphub.installation.queue.impl.executor.actions

import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.constants.FapHubConstants
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private const val FLIPPER_FAP_TMP_PATH = "${FapHubConstants.FLIPPER_TMP_FOLDER}/tmp.fap"

class FapActionUpload @Inject constructor(
    private val serviceProvider: FlipperServiceProvider
) : LogTagProvider {
    override val TAG = "FapActionUpload"

    suspend fun upload(
        fapFile: File,
        progressListener: ProgressListener
    ): String {
        info { "Start upload ${fapFile.absolutePath}" }
        val requestApi = serviceProvider.getServiceApi().requestApi
        val progressWrapper = ProgressWrapperTracker(progressListener)
        val totalLength = fapFile.length()
        var uploadedBytes = 0L
        val response = fapFile.inputStream().use { inputStream ->
            val requestFlow = streamToCommandFlow(inputStream, totalLength) { chunkData ->
                storageWriteRequest = writeRequest {
                    path = FLIPPER_FAP_TMP_PATH
                    file = file { data = chunkData }
                }
            }.map {
                FlipperRequest(
                    data = it,
                    onSendCallback = {
                        uploadedBytes += it.storageWriteRequest.pathBytes.size()
                        runBlocking {
                            progressWrapper.report(uploadedBytes, totalLength)
                        }
                    }
                )
            }

            requestApi.request(requestFlow)
        }

        if (response.commandStatus != Flipper.CommandStatus.OK) {
            error("Failed upload tmp manifest")
        }
        return FLIPPER_FAP_TMP_PATH
    }
}