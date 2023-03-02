package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.rpcinfo.api.FlipperStorageInformationApi
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.StorageStats
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StorageExistHelper @Inject constructor(
    private val flipperStorageInfoApi: FlipperStorageInformationApi
) {
    fun isExternalStorageExist(): Flow<Boolean> {
        return flipperStorageInfoApi.getStorageInformationFlow()
            .map { it.externalStorageStatus }
            .filterIsInstance<FlipperInformationStatus.Ready<StorageStats?>>()
            .map {
                info { "Storage is ${it.data}" }
                it.data is StorageStats.Loaded
            }
    }

    suspend fun invalidate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        force: Boolean
    ) {
        flipperStorageInfoApi.invalidate(scope, requestApi, force)
    }
}
