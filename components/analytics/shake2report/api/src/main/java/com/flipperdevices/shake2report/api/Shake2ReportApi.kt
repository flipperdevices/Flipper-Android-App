package com.flipperdevices.shake2report.api

import android.app.Application

interface Shake2ReportApi {
    /**
     * Call init for debug reporting in debug and internal build
     * And call empty method in release build
     */
    fun init(application: Application)

    fun reportException(
        throwable: Throwable,
        tag: String? = null,
        extras: Map<String, String>? = null
    )
}
