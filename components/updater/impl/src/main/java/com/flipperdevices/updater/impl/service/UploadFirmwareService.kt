package com.flipperdevices.updater.impl.service

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.protobuf.ProtobufConstants
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import java.io.File
import kotlinx.coroutines.flow.map

object UploadFirmwareService : LogTagProvider {
    override val TAG = "UploadFirmwareService"

    suspend fun upload(
        requestApi: FlipperRequestApi,
        folder: File,
        pathOnFlipper: String,
        onProgressUpdate: (Long, Long) -> Unit
    ) {
        val fileList = folder.walk().filterNot { it.isDirectory }.toList()
        var totalBytesSend: Long = 0
        var totalSize: Long = 0
        fileList.forEach {
            totalSize += it.length()
        }

        info { "Start upload $fileList" }

        fileList.forEach { singleFile ->
            val flipperFilePath = File(pathOnFlipper, singleFile.name).path
            singleFile.inputStream().use { inputStream ->
                val requestFlow = streamToCommandFlow(
                    inputStream,
                    singleFile.length()
                ) { chunkData ->
                    storageWriteRequest = writeRequest {
                        path = flipperFilePath
                        file = file { data = chunkData }
                    }
                }.map {
                    FlipperRequest(
                        data = it,
                        onSendCallback = {
                            totalBytesSend += ProtobufConstants.MAX_FILE_DATA
                            onProgressUpdate(totalBytesSend, totalSize)
                        }
                    )
                }
                requestApi.request(requestFlow)
            }
        }
    }
}
