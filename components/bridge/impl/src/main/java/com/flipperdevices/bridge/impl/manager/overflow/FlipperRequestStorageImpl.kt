package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.model.FlipperRequest

class FlipperRequestStorageImpl : FlipperRequestStorage {
    override val TAG = "FlipperRequestStorage"

    override fun sendRequest(vararg requests: FlipperRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun getNextRequest(timeout: Long): FlipperRequest? {
        TODO("Not yet implemented")
    }
}
