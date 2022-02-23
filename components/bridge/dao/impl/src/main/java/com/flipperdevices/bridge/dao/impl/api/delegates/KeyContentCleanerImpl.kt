package com.flipperdevices.bridge.dao.impl.api.delegates

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject

@ContributesBinding(AppGraph::class, KeyContentCleaner::class)
class KeyContentCleanerImpl @Inject constructor(
    private val keyDao: KeyDao,
    context: Context
) : KeyContentCleaner, LogTagProvider {
    override val TAG = "KeyContentCleaner"
    private val keyFolder = FlipperStorageProvider.getKeyFolder(context)

    override suspend fun deleteUnusedFiles() {
        val remainingHashes = keyFolder.listFiles()?.map { it.absolutePath }?.toHashSet() ?: return
        keyDao.getAllWithDeleted().forEach {
            val path = (it.content.flipperContent as? FlipperKeyContent.InternalFile)
                ?: return@forEach
            remainingHashes.remove(path.file.absolutePath)
        }
        info { "Found ${remainingHashes.size} file to deleted" }
        remainingHashes.forEach {
            File(it).delete()
        }
    }
}
