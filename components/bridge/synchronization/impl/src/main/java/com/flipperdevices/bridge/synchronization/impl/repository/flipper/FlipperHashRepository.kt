package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.ResultWithProgress
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.md5sumRequest
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.single

class FlipperHashRepository : LogTagProvider {
    override val TAG = "HashRepository"

    fun calculateHash(
        requestApi: FlipperRequestApi,
        keys: List<FlipperFilePath>
    ) = callbackFlow {
        val alreadyHashReceiveCounter = AtomicInteger(0)
        info { "Start request hashes for ${keys.size} keys" }

        val hashList = keys.pmap { keyPath ->
            val hash = receiveHash(requestApi, keyPath)
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
        requestApi: FlipperRequestApi,
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
