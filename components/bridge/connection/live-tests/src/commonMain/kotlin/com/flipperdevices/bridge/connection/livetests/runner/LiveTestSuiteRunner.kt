package com.flipperdevices.bridge.connection.livetests.runner

import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.LiveTestReport
import com.flipperdevices.bridge.connection.livetests.model.LiveTestStatus
import kotlin.time.TimeSource

/**
 * Executes live tests strictly one after another, so tests never
 * compete for the single serial connection to the device.
 */
class LiveTestSuiteRunner(
    private val tests: List<LiveTest>,
    private val reporter: LiveTestReporter,
    private val timeSource: TimeSource
) {
    private suspend fun runSingleTest(test: LiveTest): LiveTestReport {
        val startMark = timeSource.markNow()
        val executionResult = runCatching { test.execute() }
            .getOrElse { unexpectedError -> Result.failure(unexpectedError) }
        val status = executionResult.fold(
            onSuccess = { details -> LiveTestStatus.Passed(details) },
            onFailure = { testError -> LiveTestStatus.Failed(testError) }
        )
        return LiveTestReport(
            testName = test.name,
            status = status,
            duration = startMark.elapsedNow()
        )
    }

    suspend fun runAll(): List<LiveTestReport> {
        val reports = tests.mapIndexed { index, test ->
            reporter.onTestStarted(test, index + 1, tests.size)
            val report = runSingleTest(test)
            reporter.onTestFinished(report)
            report
        }
        reporter.onSuiteFinished(reports)
        return reports
    }
}
