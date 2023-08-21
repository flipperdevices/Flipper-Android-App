package com.flipperdevices.selfupdater.api

import com.flipperdevices.selfupdater.models.SelfUpdateResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface SelfUpdaterApi {
    fun getState(): StateFlow<Boolean>

    fun startCheckUpdate(
        scope: CoroutineScope,
        onEndCheck: suspend (SelfUpdateResult) -> Unit
    )

    fun getInstallSourceName(): String

    fun isSelfUpdateCanManualCheck(): Boolean
}
