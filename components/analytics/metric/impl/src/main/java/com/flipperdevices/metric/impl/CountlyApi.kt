package com.flipperdevices.metric.impl

interface CountlyApi {
    fun reportEvent(id: String, params: Map<String, Any>? = null)
}
