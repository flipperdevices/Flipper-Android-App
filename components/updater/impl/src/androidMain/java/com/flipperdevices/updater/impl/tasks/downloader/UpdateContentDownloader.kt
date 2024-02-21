package com.flipperdevices.updater.impl.tasks.downloader

import com.flipperdevices.updater.model.UpdateContent
import com.flipperdevices.updater.model.UpdatingState
import java.io.File

interface UpdateContentDownloader {
    fun isSupport(updateContent: UpdateContent): Boolean

    suspend fun downloadFirmwareLocal(
        updateContent: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    )
}
