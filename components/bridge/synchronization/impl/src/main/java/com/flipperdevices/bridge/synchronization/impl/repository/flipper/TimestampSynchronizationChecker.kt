package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.DetailedProgressListener
import com.flipperdevices.core.progress.DetailedProgressWrapperTracker
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.timestampRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.flowOf
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

interface TimestampSynchronizationChecker {
    data object TimestampsProgressDetail : DetailedProgressListener.Detail

    suspend fun fetchFoldersTimestamp(
        types: Array<FlipperKeyType>,
        progressTracker: DetailedProgressWrapperTracker
    ): Map<FlipperKeyType, Long?>
}

private val SUPPORTED_VERSION = SemVer(majorVersion = 0, minorVersion = 13)

@ContributesBinding(TaskGraph::class, TimestampSynchronizationChecker::class)
class TimestampSynchronizationCheckerImpl @Inject constructor(
    private val requestApi: FlipperRequestApi,
    private val flipperVersionApi: FlipperVersionApi
) : TimestampSynchronizationChecker, LogTagProvider {
    override val TAG = "TimestampSynchronizationChecker"

    override suspend fun fetchFoldersTimestamp(
        types: Array<FlipperKeyType>,
        progressTracker: DetailedProgressWrapperTracker
    ): Map<FlipperKeyType, Long?> {
        if (!flipperVersionApi.isSupported(SUPPORTED_VERSION)) {
            return types.associateWith { null }
        }

        val resultCounter = AtomicLong(0)

        progressTracker.report(
            current = resultCounter.get(),
            max = types.size.toLong(),
            detail = TimestampSynchronizationChecker.TimestampsProgressDetail
        )

        val timestampHashes = types.toList().pmap { type ->
            val response = requestApi.request(
                flowOf(
                    main {
                        storageTimestampRequest = timestampRequest {
                            path = File(Constants.KEYS_DEFAULT_STORAGE, type.flipperDir).path
                        }
                    }.wrapToRequest()
                )
            )
            progressTracker.report(
                current = resultCounter.incrementAndGet(),
                max = types.size.toLong(),
                detail = TimestampSynchronizationChecker.TimestampsProgressDetail
            )
            type to response
        }.associate { (type, response) ->
            val timestamp = if (response.hasStorageTimestampResponse()) {
                response.storageTimestampResponse.timestamp.toLong()
            } else {
                null
            }
            type to timestamp
        }
        info { "Timestamp hashes is $timestampHashes" }

        return timestampHashes
    }
}
