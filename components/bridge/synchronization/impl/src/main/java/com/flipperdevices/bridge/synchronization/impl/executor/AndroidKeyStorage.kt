package com.flipperdevices.bridge.synchronization.impl.executor

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.synchronization.impl.model.KeyPath

class AndroidKeyStorage(
    context: Context
) : AbstractKeyStorage {

    override suspend fun loadKey(keyPath: KeyPath): FlipperKeyContent {
        TODO("Not yet implemented")
    }

    override suspend fun saveKey(keyPath: KeyPath, keyContent: FlipperKeyContent) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteKey(keyPath: KeyPath) {
        TODO("Not yet implemented")
    }
}
