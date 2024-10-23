package com.flipperdevices.bridge.dao.impl.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.repository.key.DeleteKeyDao
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, KeyContentCleaner::class)
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
