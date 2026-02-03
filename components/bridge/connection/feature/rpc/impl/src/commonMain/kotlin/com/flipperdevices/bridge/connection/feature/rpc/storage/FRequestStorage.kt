package com.flipperdevices.bridge.connection.feature.rpc.storage

import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.channels.Channel
import java.util.PriorityQueue
import javax.inject.Inject

private const val QUEUE_INITIAL_CAPACITY = 11

class FRequestStorage @Inject constructor() : LogTagProvider {
    override val TAG = "FlipperRequestStorage"
    private val lock = Any()
    private val queue = PriorityQueue(
        QUEUE_INITIAL_CAPACITY,
        FRequestComparator()
    )
    // Channel to signal when new requests are available
    private val requestSignal = Channel<Unit>(Channel.CONFLATED)

    fun sendRequest(vararg requests: FlipperRequest) {
        synchronized(lock) {
            queue.addAll(requests)
            info { "Add new request. New request storage size: ${queue.size}" }
        }
        // Signal that requests are available (non-blocking, conflated)
        requestSignal.trySend(Unit)
    }

    fun removeRequest(request: FlipperRequest) {
        synchronized(lock) {
            val isRemoved = queue.remove(request)
            if (isRemoved) {
                info { "Remove request ($isRemoved). New request storage size: ${queue.size}" }
            }
        }
    }

    fun removeIf(filter: (FlipperRequest) -> Boolean) {
        synchronized(lock) {
            info {
                "Start remove from storage by filter. Current request storage size is ${queue.size}"
            }
            val notDeletedRequests = mutableListOf<FlipperRequest>()
            while (queue.isNotEmpty()) {
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
    }

    /**
     * Suspends until a request is available, then returns it.
     * This is efficient - no polling, true coroutine suspension.
     */
    suspend fun getNextRequest(): FlipperRequest? {
        while (true) {
            // Try to get from queue first
            val request = synchronized(lock) {
                queue.poll()
            }
            if (request != null) {
                info { "Remove request from queue and new queue size is ${queue.size}" }
                return request
            }
            // Queue was empty, suspend until signaled
            requestSignal.receive()
        }
    }
}
