package com.flipperdevices.bridge.connection.feature.rpc.storage

import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val QUEUE_INITIAL_CAPACITY = 11
private const val REQUEST_POOL_TIMEOUT_MS = 100L

class FRequestStorage @Inject constructor() : LogTagProvider {
    override val TAG = "FlipperRequestStorage"
    private val queue = PriorityBlockingQueue(
        QUEUE_INITIAL_CAPACITY,
        FRequestComparator()
    )

    fun sendRequest(vararg requests: FlipperRequest) {
        queue.addAll(requests)
        info { "Add new request. New request storage size: ${queue.size}" }
    }

    fun removeRequest(request: FlipperRequest) {
        val isRemoved = queue.remove(request)
        if (isRemoved) {
            info { "Remove request ($isRemoved). New request storage size: ${queue.size}" }
        }
    }

    fun removeIf(filter: (FlipperRequest) -> Boolean) {
        info {
            "Start remove from storage by filter. Current request storage size is ${queue.size}"
        }
        val notDeletedRequests = mutableListOf<FlipperRequest>()
        while (!queue.isEmpty()) {
            val request = queue.poll() ?: continue
            if (!filter(request)) {
                notDeletedRequests.add(request)
            } else {
                info { "Found request for deleted by filter and delete it" }
            }
        }
        queue.addAll(notDeletedRequests)
        info {
            "Finish remove from storage by filter. Current request storage size is ${queue.size}"
        }
    }

    suspend fun getNextRequest(timeout: Long = REQUEST_POOL_TIMEOUT_MS): FlipperRequest? {
        val request = runCatching {
            queue.poll(timeout, TimeUnit.MILLISECONDS)
        }.getOrNull()

        if (request != null) {
            info { "Remove request from queue and new queue size is ${queue.size}" }
        }

        return request
    }
}
