package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileTimestampApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressWrapperTracker
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

class TimestampSynchronizationChecker @Inject constructor(
    private val timestampApi: FFileTimestampApi
) : LogTagProvider {
    override val TAG = "TimestampSynchronizationChecker"

    suspend fun fetchFoldersTimestamp(
        types: List<FlipperKeyType>,
        progressTracker: ProgressWrapperTracker
    ): Map<FlipperKeyType, Long?> {
        val resultCounter = AtomicLong(0)

        val timestampHashes = types.toList().pmap { type ->
            val response = timestampApi.fetchFolderTimestamp(
                type.flipperDir,
                StorageRequestPriority.BACKGROUND
            )
            progressTracker.report(resultCounter.incrementAndGet(), types.size.toLong())
            type to response
        }.toMap()
        info { "Timestamp hashes is $timestampHashes" }

        return timestampHashes
    }
}
