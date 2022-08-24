package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface AbstractKeyStorage {
    suspend fun loadKey(keyPath: FlipperKeyPath): FlipperKeyContent
    suspend fun saveKey(keyPath: FlipperKeyPath, keyContent: FlipperKeyContent)
    suspend fun deleteKey(keyPath: FlipperFilePath)
}
