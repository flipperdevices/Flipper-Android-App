package com.flipperdevices.updater.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.flipperdevices.protobuf.storage.mkdirRequest
import java.io.File
import kotlinx.coroutines.flow.collect

object FolderCreateHelper {
    suspend fun recreateDirOnFlipper(requestApi: FlipperRequestApi, flipperPath: String) {
        requestApi.request(
            main {
                storageDeleteRequest = deleteRequest {
                    path = flipperPath
                    recursive = true
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).collect()

        val parentFolder = File(flipperPath).parent ?: return

        requestApi.request(
            main {
                storageMkdirRequest = mkdirRequest {
                    path = parentFolder
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).collect()

        requestApi.request(
            main {
                storageMkdirRequest = mkdirRequest {
                    path = flipperPath
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).collect()
    }
}
