package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileTimestampApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.DetailedProgressListener
import com.flipperdevices.core.progress.DetailedProgressWrapperTracker
import com.squareup.anvil.annotations.ContributesBinding
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import kotlin.io.path.Path

interface TimestampSynchronizationChecker {
    data object TimestampsProgressDetail : DetailedProgressListener.Detail

    suspend fun fetchFoldersTimestamp(
        types: Array<FlipperKeyType>,
        progressTracker: DetailedProgressWrapperTracker
    ): Map<FlipperKeyType, Long?>
}

@ContributesBinding(TaskGraph::class, TimestampSynchronizationChecker::class)
class TimestampSynchronizationCheckerImpl @Inject constructor(
    private val timestampApi: FFileTimestampApi,
) : TimestampSynchronizationChecker, LogTagProvider {
    override val TAG = "TimestampSynchronizationChecker"

    override suspend fun fetchFoldersTimestamp(
        types: Array<FlipperKeyType>,
        progressTracker: DetailedProgressWrapperTracker
    ): Map<FlipperKeyType, Long?> {
        val resultCounter = AtomicLong(0)

        progressTracker.report(
            current = resultCounter.get(),
            max = types.size.toLong(),
            detail = TimestampSynchronizationChecker.TimestampsProgressDetail
        )

        val timestampHashes = types.toList().pmap { type ->
            val response = timestampApi.fetchFolderTimestamp(
                folder = Path("/ext/").resolve(type.flipperDir).toString()
            )
            progressTracker.report(
                current = resultCounter.incrementAndGet(),
                max = types.size.toLong(),
                detail = TimestampSynchronizationChecker.TimestampsProgressDetail
            )
            type to response
        }.associate { (type, timestampOrNull) ->
            type to timestampOrNull
        }
        info { "Timestamp hashes is $timestampHashes" }

        return timestampHashes
    }
}
