package com.flipperdevices.faphub.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.mkdirRequest
import java.io.File
import kotlinx.coroutines.flow.first

private const val FLIPPER_TMP_FOLDER_PATH = ".tmp/android"

object FapHubTmpFolderProvider {
    suspend fun provideTmpFolder(requestApi: FlipperRequestApi): String {
        FLIPPER_TMP_FOLDER_PATH.split("/").runningReduce { path, folder ->
            return@runningReduce File(path, folder).absolutePath
        }.map { folderPath ->
            requestApi.request(
                main {
                    storageMkdirRequest = mkdirRequest {
                        path = File("/ext/", folderPath).absolutePath
                    }
                }.wrapToRequest()
            ) to folderPath
        }.forEach { (flow, path) ->
            val response = flow.first()
            if (response.commandStatus != Flipper.CommandStatus.OK
                && response.commandStatus != Flipper.CommandStatus.ERROR_STORAGE_EXIST
            ) {
                error("Failed create $path with command status ${response.commandStatus}")
            }
        }
        return File("/ext/", FLIPPER_TMP_FOLDER_PATH).absolutePath
    }
}