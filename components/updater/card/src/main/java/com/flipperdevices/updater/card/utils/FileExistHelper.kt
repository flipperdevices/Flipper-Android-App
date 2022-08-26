package com.flipperdevices.updater.card.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.md5sumRequest
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FileExistHelper @Inject constructor() {
    fun isFileExist(pathToFile: String, requestApi: FlipperRequestApi): Flow<Boolean> {
        return requestApi.request(
            main {
                storageMd5SumRequest = md5sumRequest {
                    path = pathToFile
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).map { response ->
            // if md5sum return not ok, we suppose file not exist
            val exist = (response.commandStatus == Flipper.CommandStatus.OK)
            info { "Exist file($pathToFile) is exist: $exist" }
            exist
        }
    }
}
