package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.utils.ProgressWrapperTracker
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.timestampRequest
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf

interface TimestampSynchronizationChecker {
    suspend fun fetchFoldersTimestamp(
        types: Array<FlipperKeyType>,
        progressTracker: ProgressWrapperTracker
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
        progressTracker: ProgressWrapperTracker
    ): Map<FlipperKeyType, Long?> {
        if (!flipperVersionApi.isSupported(SUPPORTED_VERSION)) {
            return types.associateWith { null }
        }

        val resultCounter = AtomicInteger(0)

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
            progressTracker.report(resultCounter.incrementAndGet(), types.size)
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
