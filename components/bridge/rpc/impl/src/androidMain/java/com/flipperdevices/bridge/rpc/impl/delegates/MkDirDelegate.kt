package com.flipperdevices.bridge.rpc.impl.delegates

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.mkdirRequest
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject

class MkDirDelegate @Inject constructor() {
    suspend fun mkdir(requestApi: FlipperRequestApi, pathToFolder: String) {
        pathToFolder
            .split("/")
            .filterNot { it.isBlank() }
            .runningReduce { path, folder ->
                return@runningReduce File(path, folder).path
            }
            .filterNot { isStorageName(it) }
            .map { folderPath ->
                requestApi.request(
                    main {
                        storageMkdirRequest = mkdirRequest {
                            path = File(folderPath).absolutePath
                        }
                    }.wrapToRequest()
                ) to folderPath
            }.forEach { (flow, path) ->
                val response = flow.first()
                if (response.commandStatus != Flipper.CommandStatus.OK &&
                    response.commandStatus != Flipper.CommandStatus.ERROR_STORAGE_EXIST
                ) {
                    error("Failed create $path with command status ${response.commandStatus}")
                }
            }
    }

    private fun isStorageName(path: String): Boolean {
        return if (path == "/ext" || path == "ext") {
            true
        } else {
            path == "/int" || path == "int"
        }
    }
}
