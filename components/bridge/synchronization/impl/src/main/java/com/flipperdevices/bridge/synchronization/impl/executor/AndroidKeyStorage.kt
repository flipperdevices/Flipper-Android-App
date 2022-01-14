package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

class AndroidKeyStorage(
    private val keysApi: KeyApi
) : AbstractKeyStorage {
    override suspend fun loadKey(keyPath: FlipperKeyPath): FlipperKeyContent {
        return keysApi.getKey(keyPath)?.keyContent ?: error("Can't found key $keyPath")
    }

    override suspend fun saveKey(keyPath: FlipperKeyPath, keyContent: FlipperKeyContent) {
        val flipperKey = FlipperKey(keyPath, keyContent)
        keysApi.insertKey(flipperKey)
    }

    override suspend fun deleteKey(keyPath: FlipperKeyPath) {
        keysApi.deleteKey(keyPath)
    }
}
