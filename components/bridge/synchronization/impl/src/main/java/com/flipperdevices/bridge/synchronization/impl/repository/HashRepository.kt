package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.ResultWithProgress
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.md5sumRequest
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.single

class HashRepository : LogTagProvider {
    override val TAG = "HashRepository"

    fun calculateHash(
        requestApi: FlipperRequestApi,
        keys: List<FlipperKeyPath>
    ) = callbackFlow {
        val alreadyHashReceiveCounter = AtomicInteger(0)

        val hashList = keys.map { keyPath ->
            async {
                val hash = receiveHash(requestApi, keyPath)
                send(
                    ResultWithProgress.InProgress(
                        currentPosition = alreadyHashReceiveCounter.incrementAndGet(),
                        maxPosition = keys.size
                    )
                )
                return@async KeyWithHash(keyPath, hash)
            }
        }.map { it.await() }

        send(ResultWithProgress.Completed(hashList))
        close()
    }

    private suspend fun receiveHash(
        requestApi: FlipperRequestApi,
        keyPath: FlipperKeyPath
    ): String {
        return requestApi.request(
            main {
                storageMd5SumRequest = md5sumRequest {
                    path = keyPath.pathToKey
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).single().storageMd5SumResponse.md5Sum
    }
}
