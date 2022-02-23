package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info

class AndroidKeyStorage(
    private val keysApi: KeyApi
) : AbstractKeyStorage, LogTagProvider {
    override val TAG = "AndroidKeyStorage"

    override suspend fun loadKey(keyPath: FlipperKeyPath): FlipperKeyContent {
        info { "Load key $keyPath" }
        return keysApi.getKey(keyPath)?.keyContent ?: error("Can't found key $keyPath")
    }

    override suspend fun saveKey(keyPath: FlipperKeyPath, keyContent: FlipperKeyContent) {
        info { "Save key $keyPath with $keyContent" }
        val flipperKey = FlipperKey(keyPath, keyContent)
        keysApi.insertKey(flipperKey)
    }

    override suspend fun deleteKey(keyPath: FlipperKeyPath) {
        info { "Mark delete key $keyPath" }
        keysApi.markDeleted(keyPath)
    }
}
