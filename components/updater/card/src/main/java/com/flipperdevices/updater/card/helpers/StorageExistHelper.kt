package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.statRequest
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val FLIPPER_PATH_EXTERNAL_STORAGE = "/ext/"

class StorageExistHelper @Inject constructor() {
    fun isExternalStorageExist(requestApi: FlipperRequestApi): Flow<Boolean> {
        return requestApi.request(
            main {
                storageStatRequest = statRequest {
                    path = FLIPPER_PATH_EXTERNAL_STORAGE
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).map { response ->
            // if statRequest return not ok, we suppose storage not exist
            val exist = (response.commandStatus == Flipper.CommandStatus.OK)
            info { "Exist storage($FLIPPER_PATH_EXTERNAL_STORAGE) is exist: $exist" }
            exist
        }
    }
}