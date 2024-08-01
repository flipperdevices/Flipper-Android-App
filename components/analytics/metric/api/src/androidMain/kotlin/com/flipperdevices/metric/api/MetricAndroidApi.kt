package com.flipperdevices.metric.api

import com.flipperdevices.metric.api.events.SessionState

interface MetricAndroidApi : MetricApi {
    fun reportSessionState(state: SessionState)
}
