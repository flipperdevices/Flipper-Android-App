package com.flipperdevices.selfupdater.api

import com.flipperdevices.selfupdater.models.SelfUpdateResult

interface SelfUpdaterApi {
    suspend fun startCheckUpdate(
        onEndCheck: suspend (SelfUpdateResult) -> Unit
    )

    fun getInstallSourceName(): String

    fun isSelfUpdateChecked(): Boolean
}
