package com.flipperdevices.bridge.synchronization.impl.executor

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

class AndroidKeyStorage(
    context: Context
) : AbstractKeyStorage {

    override suspend fun loadKey(keyPath: FlipperKeyPath): FlipperKeyContent {
        TODO("Not yet implemented")
    }

    override suspend fun saveKey(keyPath: FlipperKeyPath, keyContent: FlipperKeyContent) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteKey(keyPath: FlipperKeyPath) {
        TODO("Not yet implemented")
    }
}
