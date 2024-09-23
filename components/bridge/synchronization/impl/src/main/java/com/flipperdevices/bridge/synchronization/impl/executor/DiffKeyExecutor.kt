package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.DetailedProgressListener
import com.flipperdevices.core.progress.DetailedProgressWrapperTracker
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface DiffKeyExecutor {
    data class DiffProgressDetail(
        val fileName: String
    ) : DetailedProgressListener.Detail

    suspend fun executeBatch(
        source: AbstractKeyStorage,
        target: AbstractKeyStorage,
        diffs: List<KeyDiff>,
        tracker: DetailedProgressWrapperTracker
    ): List<KeyDiff>
}

/**
 * This class execute diff for
 */
@ContributesBinding(TaskGraph::class, DiffKeyExecutor::class)
class DiffKeyExecutorImpl @Inject constructor() : DiffKeyExecutor, LogTagProvider {
    override val TAG = "DiffKeyExecutor"

    /**
     * @return list of diffs which successful applied
     */
    override suspend fun executeBatch(
        source: AbstractKeyStorage,
        target: AbstractKeyStorage,
        diffs: List<KeyDiff>,
        tracker: DetailedProgressWrapperTracker
    ): List<KeyDiff> {
        return diffs.sortedWith(
            compareBy(
                { it.newHash.keyPath.fileType },
                { it.action }
            )
        ).mapIndexedNotNull { index, diff ->
            try {
                info { "Execute $diff for $source to $target" }

                tracker.report(
                    index.toLong(),
                    diffs.size.toLong(),
                    DiffKeyExecutor.DiffProgressDetail(diff.newHash.keyPath.nameWithExtension)
                )
                execute(source, target, diff)
                return@mapIndexedNotNull diff
            } catch (executeError: Exception) {
                error(executeError) { "While apply diff $diff we have error" }
            }
            return@mapIndexedNotNull null
        }
    }

    private suspend fun execute(
        source: AbstractKeyStorage,
        target: AbstractKeyStorage,
        diff: KeyDiff
    ) {
        val path = diff.newHash.keyPath
        val folder = when (path.fileType) {
            FlipperFileType.KEY -> {
                @Suppress("UnsafeCallOnNullableType")
                path.keyType!!.flipperDir
            }

            FlipperFileType.SHADOW_NFC -> FlipperKeyType.NFC.flipperDir
            FlipperFileType.UI_INFRARED -> FlipperKeyType.INFRARED.flipperDir
            FlipperFileType.OTHER -> error("Don't support file with this type")
        }
        val targetPath = FlipperFilePath(
            folder,
            path.nameWithExtension
        )

        when (diff.action) {
            KeyAction.ADD -> {
                val content = source.loadFile(path)
                target.saveFile(targetPath, content)
            }

            KeyAction.MODIFIED -> {
                val content = source.loadFile(path)
                target.modify(targetPath, content)
            }

            KeyAction.DELETED -> target.deleteFile(targetPath)
        }
    }
}
