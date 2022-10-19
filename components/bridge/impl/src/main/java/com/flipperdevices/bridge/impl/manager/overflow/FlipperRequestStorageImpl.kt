package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit

private const val QUEUE_INITIAL_CAPACITY = 11

class FlipperRequestStorageImpl : FlipperRequestStorage {
    override val TAG = "FlipperRequestStorage"
    private val queue = PriorityBlockingQueue(QUEUE_INITIAL_CAPACITY, FlipperRequestComparator())

    override fun sendRequest(vararg requests: FlipperRequest) {
        verbose { "Request $requests" }
        queue.addAll(requests)
        info { "Add new request. New request storage size: ${queue.size}" }
    }

    override fun removeRequest(request: FlipperRequest) {
        val isRemoved = queue.remove(request)
        if (isRemoved) {
            info { "Remove request ($isRemoved). New request storage size: ${queue.size}" }
        }
    }

    override fun removeIf(filter: (FlipperRequest) -> Boolean) {
        info {
            "Start remove from storage by filter. Current request storage size is ${queue.size}"
        }
        val notDeletedRequests = mutableListOf<FlipperRequest>()
        while (!queue.isEmpty()) {
            val request = queue.poll() ?: continue
            if (!filter(request)) {
                notDeletedRequests.add(request)
            } else info { "Found request for deleted by filter and delete it" }
        }
        queue.addAll(notDeletedRequests)
        info {
            "Finish remove from storage by filter. Current request storage size is ${queue.size}"
        }
    }

    override suspend fun getNextRequest(timeout: Long): FlipperRequest? {
        val request = runCatching {
            queue.poll(timeout, TimeUnit.MILLISECONDS)
        }.getOrNull()

        if (request != null) {
            info { "Remove request from queue and new queue size is ${queue.size}" }
        }

        return request
    }
}
