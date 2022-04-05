package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.shake2report.api.Shake2ReportApi

/**
 * This class execute diff for
 */
class DiffKeyExecutor(
    private val reportApi: Shake2ReportApi
) : LogTagProvider {
    override val TAG = "DiffKeyExecutor"

    /**
     * @return list of diffs which successful applied
     */
    suspend fun executeBatch(
        source: AbstractKeyStorage,
        target: AbstractKeyStorage,
        diffs: List<KeyDiff>
    ): List<KeyDiff> {
        return diffs.mapNotNull {
            try {
                info { "Execute $it for $source to $target" }
                execute(source, target, it)
                return@mapNotNull it
            } catch (
                @Suppress("detekt:TooGenericExceptionCaught")
                executeError: Exception
            ) {
                error(executeError) { "While apply diff $it we have error" }
                reportApi.reportException(
                    executeError,
                    "diffkeyexecutor",
                    mapOf("path" to it.newHash.keyPath.toString())
                )
            }
            return@mapNotNull null
        }
    }

    private suspend fun execute(
        source: AbstractKeyStorage,
        target: AbstractKeyStorage,
        diff: KeyDiff
    ) {
        val path = diff.newHash.keyPath
        val targetPath = FlipperKeyPath(
            path.fileType!!.flipperDir,
            path.name,
            deleted = false
        )

        when (diff.action) {
            KeyAction.ADD -> {
                val content = source.loadKey(path)
                target.saveKey(targetPath, content)
            }
            KeyAction.MODIFIED -> {
                val content = source.loadKey(path)
                target.deleteKey(targetPath)
                target.saveKey(targetPath, content)
            }
            KeyAction.DELETED -> target.deleteKey(targetPath)
        }
    }
}
