package com.flipperdevices.analytics.shake2report.impl

import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface InternalShake2Report {
    val logDir: File

    fun register()
    fun setExtra(tags: List<Pair<String, String>>)
    fun getIsRegisteredFlow(): StateFlow<Boolean>
}
