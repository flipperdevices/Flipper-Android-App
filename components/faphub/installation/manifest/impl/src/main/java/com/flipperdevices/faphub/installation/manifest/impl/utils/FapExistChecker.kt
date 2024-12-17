package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.pathString

class FapExistChecker @Inject constructor(
    private val fFeatureProvider: FFeatureProvider
) : LogTagProvider {
    override val TAG = "FapExistChecker"

    private val cacheFolderToPaths = mutableMapOf<String, List<String>>()
    private val mutex = Mutex()

    suspend fun checkExist(path: String): Boolean = withLockResult(mutex, "check") {
        val folder = Path(path).parent?.pathString ?: "/"

        val fileList = cacheFolderToPaths[folder]
            ?: fFeatureProvider.getSync<FStorageFeatureApi>()
                ?.listingApi()
                ?.ls(folder)
                ?.getOrNull()
                .orEmpty()
                .map { item -> Path(folder).resolve(item.fileName).toString() }
                .also { paths -> cacheFolderToPaths[folder] = paths }

        return@withLockResult fileList.contains(path)
    }

    suspend fun invalidate() = withLock(mutex, "invalidate") {
        cacheFolderToPaths.clear()
    }
}
