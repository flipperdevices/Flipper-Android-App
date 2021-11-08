package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.model.FlipperRequest
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit

private const val QUEUE_INITIAL_CAPACITY = 11

class FlipperRequestStorageImpl : FlipperRequestStorage {
    override val TAG = "FlipperRequestStorage"
    private val queue = PriorityBlockingQueue(QUEUE_INITIAL_CAPACITY, FlipperRequestComparator())

    override fun sendRequest(vararg requests: FlipperRequest) {
        queue.addAll(requests)
    }

    override suspend fun getNextRequest(timeout: Long): FlipperRequest? {
        return runCatching {
            queue.poll(timeout, TimeUnit.MILLISECONDS)
        }.getOrNull()
    }
}
