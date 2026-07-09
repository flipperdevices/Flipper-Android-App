package com.flipperdevices.bridge.connection.livetests.runner

import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.LiveTestReport

interface LiveTestReporter {
    fun onTestStarted(test: LiveTest, testNumber: Int, totalCount: Int)
    fun onTestFinished(report: LiveTestReport)
    fun onSuiteFinished(reports: List<LiveTestReport>)
}
