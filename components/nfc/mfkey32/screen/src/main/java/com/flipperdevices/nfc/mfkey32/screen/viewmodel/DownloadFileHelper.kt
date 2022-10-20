package com.flipperdevices.nfc.mfkey32.screen.viewmodel

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import java.io.File
import java.io.FileNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DownloadFileHelper {
    suspend fun downloadFile(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String,
        file: File,
        onUpdateIncrement: (Long) -> Unit = {}
    ) = withContext(Dispatchers.Default) {
        requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = pathOnFlipper
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).collect {
            if (!it.hasStorageReadResponse()) throw FileNotFoundException()
            val data = it.storageReadResponse.file.data
            file.appendBytes(data.toByteArray())
            onUpdateIncrement(data.size().toLong())
        }
    }
}
