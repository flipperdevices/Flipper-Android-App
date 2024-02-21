package com.flipperdevices.core.ui.lifecycle

import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

/**
 * This class allows you to run one and only one task
 * at a time with coroutine scope and flipper ble
 */
abstract class OneTimeExecutionBleTask<INPUT, STATE>(
    private val serviceProvider: FlipperServiceProvider
) : TaskWithLifecycle(), LogTagProvider {
    private val taskScope = coroutineScope(Dispatchers.Default)
    private val mutex = Mutex()
    private var job: Job? = null
    private val isAlreadyLaunched = AtomicBoolean(false)

    fun start(
        input: INPUT,
        stateListener: suspend (STATE) -> Unit
    ) = taskScope.launch(Dispatchers.Main) {
        info { "Called start" }
        if (!isAlreadyLaunched.compareAndSet(false, true)) {
            warn { "OneTimeExecutionBleTask call again" }
            return@launch
        }
        serviceProvider.provideServiceApi(this@OneTimeExecutionBleTask) { serviceApi ->
            info { "Flipper service provided" }
            launchWithLock(mutex, taskScope) {
                job?.cancelAndJoin()
                job = null
                job = taskScope.launch(Dispatchers.Default) {
                    val localScope = this
                    // Waiting to be connected to the flipper
                    try {
                        startInternal(localScope, serviceApi, input, stateListener)
                    } catch (throwable: Throwable) {
                        error(throwable) { "Error during execution" }
                        withContext(Dispatchers.Main) {
                            onStop()
                        }
                    }
                }
            }
        }
        onStart()
        taskScope.launch(Dispatchers.Default) {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    info { "onStopAsync" }
                    onStopAsync(stateListener)
                }
            }
        }
    }

    abstract suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: INPUT,
        stateListener: suspend (STATE) -> Unit
    )

    abstract suspend fun onStopAsync(stateListener: suspend (STATE) -> Unit)
}
