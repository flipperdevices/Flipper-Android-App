package com.flipperdevices.bridge.dao.impl.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.repository.key.DeleteKeyDao
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import dev.zacsweers.metro.ContributesBinding
import java.io.File
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<KeyContentCleaner>())
class KeyContentCleanerImpl @Inject constructor(
    private val deleteKeyDaoProvider: Provider<DeleteKeyDao>,
    flipperStorageProvider: FlipperStorageProvider
) : KeyContentCleaner, LogTagProvider {
    override val TAG = "KeyContentCleaner"
    private val keyFolder = flipperStorageProvider.getKeyFolder().toFile()

    private val deleteKeyDao by deleteKeyDaoProvider

    override suspend fun deleteUnusedFiles() {
        val remainingHashes = keyFolder.listFiles()?.map { it.absolutePath }?.toHashSet() ?: return
        deleteKeyDao.getAllWithDeleted().forEach {
            val path = (it.content.flipperContent as? FlipperKeyContent.InternalFile)
                ?: return@forEach
            remainingHashes.remove(path.path)
        }
        info { "Found ${remainingHashes.size} file to deleted" }
        remainingHashes.forEach {
            File(it).delete()
        }
    }
}
