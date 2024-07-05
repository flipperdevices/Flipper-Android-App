package com.flipperdevices.remotecontrols.impl.setup.api.save.file

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.deeplink.model.DeeplinkContent
import kotlinx.coroutines.flow.Flow

interface SaveFileApi {
    fun save(
        requestApi: FlipperRequestApi,
        deeplinkContent: DeeplinkContent,
        ffPath: FlipperFilePath
    ): Flow<Status>

    sealed interface Status {
        data class Saving(val uploaded: Long, val size: Long) : Status
        data object Finished : Status
    }
}
