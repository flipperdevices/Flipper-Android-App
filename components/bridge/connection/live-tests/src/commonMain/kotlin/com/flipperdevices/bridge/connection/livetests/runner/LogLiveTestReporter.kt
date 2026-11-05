package com.flipperdevices.bridge.connection.livetests.runner

import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.LiveTestReport
import com.flipperdevices.bridge.connection.livetests.model.LiveTestStatus
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlin.time.Duration

class LogLiveTestReporter : LiveTestReporter, LogTagProvider {
    override val TAG = "LiveTests"

    override fun onTestStarted(test: LiveTest, testNumber: Int, totalCount: Int) {
        info { "[$testNumber/$totalCount] ${test.name}: ${test.description}" }
    }

    override fun onTestFinished(report: LiveTestReport) {
        when (val status = report.status) {
            is LiveTestStatus.Passed ->
                info { "PASS '${report.testName}' in ${report.duration}: ${status.details}" }

            is LiveTestStatus.Failed ->
                error(status.error) { "FAIL '${report.testName}' in ${report.duration}" }
        }
    }

    override fun onSuiteFinished(reports: List<LiveTestReport>) {
        val passedCount = reports.count { report -> report.status is LiveTestStatus.Passed }
        val totalDuration = reports.fold(Duration.ZERO) { accumulated, report ->
            accumulated + report.duration
        }
        info { "Live tests finished: $passedCount/${reports.size} passed in $totalDuration" }
        reports
            .filter { report -> report.status is LiveTestStatus.Failed }
            .forEach { report -> info { "Failed test: ${report.testName}" } }
    }
}
