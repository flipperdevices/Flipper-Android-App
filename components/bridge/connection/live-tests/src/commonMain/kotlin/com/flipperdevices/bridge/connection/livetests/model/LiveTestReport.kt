package com.flipperdevices.bridge.connection.livetests.model

import kotlin.time.Duration

data class LiveTestReport(
    val testName: String,
    val status: LiveTestStatus,
    val duration: Duration
)
