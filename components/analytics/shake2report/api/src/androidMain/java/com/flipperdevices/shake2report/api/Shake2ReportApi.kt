package com.flipperdevices.shake2report.api

import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import kotlinx.coroutines.flow.StateFlow

interface Shake2ReportApi {
    /**
     * Call init for debug reporting in debug and internal build
     * And call empty method in release build
     */
    fun init()

    fun updateGattInformation(gattInformation: FlipperGATTInformation)
    fun setExtra(tags: List<Pair<String, String>>)

    fun reportException(
        throwable: Throwable,
        tag: String? = null,
        extras: Map<String, String>? = null
    )

    fun isInitialized(): StateFlow<Boolean>
}
