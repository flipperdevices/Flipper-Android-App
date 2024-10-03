package com.flipperdevices.core.ktx.jre

import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun <T> LogTagProvider.runBlockingWithLog(
    tag: String? = null,
    block: suspend CoroutineScope.() -> T
): T {
    var startTime: Long = 0
    if (BuildKonfig.IS_LOG_ENABLED) {
        startTime = System.currentTimeMillis()
    }
    return runBlocking {
        verbose { "Waiting time for job $tag is ${System.currentTimeMillis() - startTime}ms" }
        verbose {
            startTime = System.currentTimeMillis()
            "Launch $tag job in blocking mode..."
        }
        val result = block()
        verbose { "Complete $tag job in ${System.currentTimeMillis() - startTime}ms" }
        return@runBlocking result
    }
}

fun LogTagProvider.launchWithLock(
    mutex: Mutex,
    scope: CoroutineScope,
    tag: String? = null,
    action: suspend CoroutineScope.() -> Unit
) {
    scope.launch(FlipperDispatchers.workStealingDispatcher) {
        withLock(mutex, tag) { action.invoke(this) }
    }
}

suspend fun LogTagProvider.withLock(
    mutex: Mutex,
    tag: String? = null,
    action: suspend () -> Unit
): Unit = withLockResult(mutex, tag, action)

suspend fun <T> LogTagProvider.withLockResult(
    mutex: Mutex,
    tag: String? = null,
    action: suspend () -> T
): T {
    if (mutex.isLocked) {
        info { "I can't execute right now job $tag because $mutex is locked" }
    }
    return mutex.withLock {
        var startTime: Long = 0
        verbose {
            startTime = System.currentTimeMillis()
            "Launch $tag job in mutex mode..."
        }
        val result = action()
        verbose { "Complete $tag job in ${System.currentTimeMillis() - startTime}ms" }
        return@withLock result
    }
}
