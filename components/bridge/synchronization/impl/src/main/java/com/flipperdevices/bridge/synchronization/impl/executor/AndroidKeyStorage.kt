package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info

class AndroidKeyStorage(
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi
) : AbstractKeyStorage, LogTagProvider {
    override val TAG = "AndroidKeyStorage"

    override suspend fun loadKey(keyPath: FlipperKeyPath): FlipperKeyContent {
        info { "Load key $keyPath" }
        return simpleKeyApi.getKey(keyPath)?.keyContent ?: error("Can't found key $keyPath")
    }

    override suspend fun saveKey(keyPath: FlipperKeyPath, keyContent: FlipperKeyContent) {
        info { "Save key $keyPath with $keyContent" }
        val flipperKey = FlipperKey(keyPath, keyContent, synchronized = true)
        simpleKeyApi.insertKey(flipperKey)
    }

    override suspend fun deleteKey(keyPath: FlipperKeyPath) {
        info { "Mark delete key $keyPath" }
        deleteKeyApi.markDeleted(keyPath)
    }
}
