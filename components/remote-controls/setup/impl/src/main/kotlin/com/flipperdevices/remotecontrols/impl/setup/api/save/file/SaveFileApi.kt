package com.flipperdevices.remotecontrols.impl.setup.api.save.file

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import kotlinx.coroutines.flow.Flow

interface SaveFileApi {
    fun save(
        requestApi: FlipperRequestApi,
        textContent: String,
        absolutePath: String
    ): Flow<Status>

    sealed interface Status {
        data class Saving(
            val uploaded: Long,
            val size: Long,
            val lastWriteSize: Long
        ) : Status

        data object Finished : Status
    }
}
