package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.synchronization.impl.model.KeyPath

interface AbstractKeyStorage {
    suspend fun loadKey(keyPath: KeyPath): FlipperKeyContent
    suspend fun saveKey(keyPath: KeyPath, keyContent: FlipperKeyContent)
    suspend fun deleteKey(keyPath: KeyPath)
}
