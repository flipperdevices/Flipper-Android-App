package com.flipperdevices.shake2report.api

import kotlinx.coroutines.flow.StateFlow

interface Shake2ReportApi {
    /**
     * Call init for debug reporting in debug and internal build
     * And call empty method in release build
     */
    fun init()

    fun setExtra(tags: List<Pair<String, String>>)

    fun reportException(
        throwable: Throwable,
        tag: String? = null,
        extras: Map<String, String>? = null
    )

    fun isInitialized(): StateFlow<Boolean>
}
