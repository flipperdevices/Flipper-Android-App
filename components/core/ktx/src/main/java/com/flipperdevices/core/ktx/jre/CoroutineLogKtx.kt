package com.flipperdevices.core.ktx.jre

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

fun <T> LogTagProvider.runBlockingWithLog(
    tag: String? = null,
    block: suspend CoroutineScope.() -> T
): T {
    var startTime: Long = 0
    verbose {
        startTime = System.currentTimeMillis()
        "Launch $tag job in blocking mode..."
    }
    val result = runBlocking {
        return@runBlocking block()
    }
    verbose { "Complete $tag job in ${System.currentTimeMillis() - startTime}ms" }
    return result
}
