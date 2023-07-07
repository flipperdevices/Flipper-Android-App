package com.flipperdevices.metric.impl.countly

interface CountlyApi {
    fun reportEvent(id: String, params: Map<String, Any?>? = null)
}
