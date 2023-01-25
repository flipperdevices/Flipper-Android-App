package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.ResultWithProgress
import com.flipperdevices.bridge.synchronization.impl.model.trackProgressAndReturn
import com.flipperdevices.bridge.synchronization.impl.utils.ProgressWrapperTracker
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.md5sumRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.single
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

interface FlipperHashRepository {
    suspend fun getHashesForType(
        flipperKeyType: FlipperKeyType,
        tracker: ProgressWrapperTracker
    ): List<KeyWithHash>
}

@ContributesBinding(TaskGraph::class, FlipperHashRepository::class)
class FlipperHashRepositoryImpl @Inject constructor(
    private val requestApi: FlipperRequestApi,
    private val keysListingRepository: KeysListingRepository
) : FlipperHashRepository, LogTagProvider {
    override val TAG = "HashRepository"

    override suspend fun getHashesForType(
        flipperKeyType: FlipperKeyType,
        tracker: ProgressWrapperTracker
    ): List<KeyWithHash> {
        val flipperKeys = keysListingRepository.getKeysForType(flipperKeyType)
        tracker.onProgress(current = 0.3f)
        val hashProgressTracker = ProgressWrapperTracker(
            min = 0.3f,
            max = 1f,
            progressListener = tracker
        )

        return calculateHash(flipperKeys).trackProgressAndReturn {
            // Progress
            hashProgressTracker.report(it.currentPosition, it.maxPosition)
        }
    }

    private fun calculateHash(
        keys: List<FlipperFilePath>
    ) = callbackFlow {
        val alreadyHashReceiveCounter = AtomicInteger(0)
        info { "Start request hashes for ${keys.size} keys" }

        val hashList = keys.pmap { keyPath ->
            val hash = receiveHash(keyPath)
            send(
                ResultWithProgress.InProgress(
                    currentPosition = alreadyHashReceiveCounter.incrementAndGet(),
                    maxPosition = keys.size
                )
            )
            return@pmap KeyWithHash(keyPath, hash)
        }

        send(ResultWithProgress.Completed(hashList))
        close()
    }

    private suspend fun receiveHash(
        keyPath: FlipperFilePath
    ): String {
        return requestApi.request(
            main {
                storageMd5SumRequest = md5sumRequest {
                    path = keyPath.getPathOnFlipper()
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).single().storageMd5SumResponse.md5Sum
    }
}
