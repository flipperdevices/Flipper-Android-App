package com.flipperdevices.bridge.synchronization.impl.repository.android

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.core.ktx.jre.md5
import com.flipperdevices.core.ktx.jre.pmap
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

interface AndroidHashRepository {
    suspend fun getHashes(keys: List<FlipperKey>): List<KeyWithHash>
}

@ContributesBinding(TaskGraph::class, binding<AndroidHashRepository>())
class AndroidHashRepositoryImpl @Inject constructor() : AndroidHashRepository {
    override suspend fun getHashes(keys: List<FlipperKey>): List<KeyWithHash> {
        return keys.pmap { flipperKey ->
            flipperKey.additionalFiles.plus(flipperKey.mainFile).map {
                KeyWithHash(
                    it.path,
                    it.content.openStream().md5()
                )
            }
        }.flatten()
    }
}
