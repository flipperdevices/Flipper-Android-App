package com.flipperdevices.updater.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.mkdirRequest
import kotlinx.coroutines.flow.collect
import java.io.File

object FolderCreateHelper {
    suspend fun mkdirFolderOnFlipper(requestApi: FlipperRequestApi, flipperPath: String) {
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
