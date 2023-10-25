package com.flipperdevices.metric.impl.countly

import com.flipperdevices.metric.api.events.SessionState

interface CountlyApi {
    fun reportEvent(id: String, params: Map<String, Any?>? = null)
    fun reportSessionState(state: SessionState)
}
