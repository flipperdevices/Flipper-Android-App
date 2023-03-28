package com.flipperdevices.analytics.shake2report.impl

import java.io.File
import kotlinx.coroutines.flow.StateFlow

interface InternalShake2Report {
    val logDir: File

    fun register()
    fun setExtra(tags: List<Pair<String, String>>)
    fun getIsRegisteredFlow(): StateFlow<Boolean>
}
