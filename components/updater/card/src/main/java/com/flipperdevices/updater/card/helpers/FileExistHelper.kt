package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.core.log.info
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FileExistHelper @Inject constructor() {
    fun isFileExist(pathToFile: String, fListingStorageApi: FListingStorageApi): Flow<Boolean> {
        return flow {
            fListingStorageApi.lsWithMd5Flow(pathToFile)
                .map { it.getOrNull().orEmpty().isNotEmpty() }
                .onEach { isExist ->
                    info { "Exist file($pathToFile) is exist: $isExist" }
                    emit(isExist)
                }.collect()
        }
    }
}
