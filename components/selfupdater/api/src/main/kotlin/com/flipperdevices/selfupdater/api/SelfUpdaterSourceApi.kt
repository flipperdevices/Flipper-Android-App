package com.flipperdevices.selfupdater.api

import com.flipperdevices.selfupdater.models.SelfUpdateResult

interface SelfUpdaterSourceApi {
    suspend fun checkUpdate(manual: Boolean): SelfUpdateResult

    fun getInstallSourceName(): String

    fun isSelfUpdateCanManualCheck(): Boolean
}
