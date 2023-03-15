package com.flipperdevices.analytics.shake2report.impl

import java.io.File

interface InternalShake2Report {
    val logDir: File

    fun register()
    fun setExtra(tags: List<Pair<String, String>>)
}
