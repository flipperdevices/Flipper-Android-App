package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

interface TimestampSynchronizationChecker {
    suspend fun shouldSkipSynchronization(): Boolean
}

private const val VERSION_WAITING_TIMEOUT_MS = 10 * 1000L // 10 sec
private val SUPPORTED_VERSION = SemVer(majorVersion = 0, minorVersion = 13)

@ContributesBinding(TaskGraph::class, TimestampSynchronizationChecker::class)
class TimestampSynchronizationCheckerImpl @Inject constructor(
    private val requestApi: FlipperRequestApi,
    private val flipperVersionApi: FlipperVersionApi
) : TimestampSynchronizationChecker, LogTagProvider {
    override val TAG = "TimestampSynchronizationChecker"

    override suspend fun shouldSkipSynchronization(): Boolean {
        val flipperVersion = try {
            withTimeout(VERSION_WAITING_TIMEOUT_MS) {
                flipperVersionApi.getVersionInformationFlow()
                    .filterNotNull()
                    .first()
            }
        } catch (exception: Throwable) {
            error(exception) { "Failed receive flipper version" }
            return false
        }

        if (flipperVersion < SUPPORTED_VERSION) {
            return false
        }

        return true
    }

}