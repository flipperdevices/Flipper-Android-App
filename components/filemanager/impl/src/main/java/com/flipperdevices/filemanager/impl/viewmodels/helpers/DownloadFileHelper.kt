package com.flipperdevices.filemanager.impl.viewmodels.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadFileHelper {
    suspend fun downloadFile(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String,
        file: File,
        onUpdateIncrement: (Long) -> Unit
    ) = withContext(Dispatchers.Default) {
        requestApi.request(getRequest(pathOnFlipper)).collect {
            val data = it.storageReadResponse.file.data
            file.appendBytes(data.toByteArray())
            onUpdateIncrement(data.size().toLong())
        }
    }

    private fun getRequest(pathOnFlipper: String): FlipperRequest = main {
        storageReadRequest = readRequest {
            path = pathOnFlipper
        }
    }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
}
