package com.flipperdevices.bridge.connection.livetests

import com.flipperdevices.bridge.connection.livetests.model.LiveTestReport
import com.flipperdevices.bridge.connection.livetests.runner.LiveTestSuiteRunner
import com.flipperdevices.bridge.connection.livetests.runner.LogLiveTestReporter
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.flow.first
import dev.zacsweers.metro.Inject
import kotlin.time.TimeSource

/**
 * Entry point of the live test suite.
 *
 * Waits until a Flipper Zero is connected via the orchestrator,
 * then runs every live test sequentially and reports results to logs.
 */
class RootLiveTest @Inject constructor(
    private val orchestrator: FDeviceOrchestrator,
    liveTestsSuite: LiveTestsSuite
) : LogTagProvider {
    override val TAG = "RootLiveTest"

    private val suiteRunner = LiveTestSuiteRunner(
        tests = liveTestsSuite.orderedTests,
        reporter = LogLiveTestReporter(),
        timeSource = TimeSource.Monotonic
    )

    suspend fun awaitDeviceAndRunAll(): List<LiveTestReport> {
        info { "Waiting for a connected Flipper to start live tests" }
        orchestrator.getState().first { status -> status is FDeviceConnectStatus.Connected }
        info { "Device connected, starting live tests" }
        return suiteRunner.runAll()
    }
}
