package com.flipperdevices.bridge.api.manager.service

import com.flipperdevices.core.data.SemVer
import kotlinx.coroutines.flow.StateFlow

private const val VERSION_WAITING_TIMEOUT_MS = 30 * 1000L // 10 sec

interface FlipperVersionApi {
    fun getVersionInformationFlow(): StateFlow<SemVer?>

    suspend fun isSupported(
        version: SemVer,
        timeout: Long = VERSION_WAITING_TIMEOUT_MS
    ): Boolean
}
