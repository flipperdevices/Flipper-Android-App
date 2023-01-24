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
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.timestampRequest
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withTimeout

interface TimestampSynchronizationChecker {
    suspend fun fetchFoldersTimestamp(
        types: Array<FlipperKeyType>,
        block: suspend (type: FlipperKeyType, timestampMs: Long?) -> Unit
    )
}

private const val VERSION_WAITING_TIMEOUT_MS = 10 * 1000L // 10 sec
private val SUPPORTED_VERSION = SemVer(majorVersion = 0, minorVersion = 13)

@ContributesBinding(TaskGraph::class, TimestampSynchronizationChecker::class)
class TimestampSynchronizationCheckerImpl @Inject constructor(
    private val requestApi: FlipperRequestApi,
    private val flipperVersionApi: FlipperVersionApi
) : TimestampSynchronizationChecker, LogTagProvider {
    override val TAG = "TimestampSynchronizationChecker"

    override suspend fun fetchFoldersTimestamp(
        types: Array<FlipperKeyType>,
        block: suspend (type: FlipperKeyType, timestampMs: Long?) -> Unit
    ) {
        val flipperVersion = try {
            withTimeout(VERSION_WAITING_TIMEOUT_MS) {
                flipperVersionApi.getVersionInformationFlow()
                    .filterNotNull()
                    .first()
            }
        } catch (exception: Throwable) {
            error(exception) { "Failed receive flipper version" }
            types.forEach { block(it, null) }
            return
        }

        if (flipperVersion < SUPPORTED_VERSION) {
            types.forEach { block(it, null) }
            return
        }


        val timestampHashes = types.toList().pmap { type ->
            type to requestApi.request(
                flowOf(
                    main {
                        storageTimestampRequest = timestampRequest {
                            path = File(Constants.KEYS_DEFAULT_STORAGE, type.flipperDir).path
                        }
                    }.wrapToRequest()
                )
            )
        }
        info { "Timestamp hashes is $timestampHashes" }

        timestampHashes.forEach { (type, response) ->
            val timestamp = if (response.hasStorageTimestampResponse()) {
                response.storageTimestampResponse.timestamp.toLong()
            } else {
                null
            }
            block(type, timestamp)
        }
    }
}