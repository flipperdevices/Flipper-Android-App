package com.flipperdevices.unhandledexception.impl.api

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.unhandledexception.api.UnhandledExceptionApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

private const val BLE_EXCEPTION_PREFIX = "Need BLUETOOTH PRIVILEGED permission"

@Singleton
@ContributesBinding(AppGraph::class, UnhandledExceptionApi::class)
class UnhandledExceptionImpl @Inject constructor(
    private val dataStoreProvider: Provider<DataStore<Settings>>
) : UnhandledExceptionApi, Thread.UncaughtExceptionHandler, LogTagProvider {
    override val TAG = "UnhandledException"
    private var previousExceptionHandler: Thread.UncaughtExceptionHandler? = null
    private var initialized = false

    override fun initExceptionHandler() {
        if (initialized) {
            return
        }
        previousExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        initialized = true
    }

    override fun isBleConnectionForbiddenFlow(): Flow<Boolean> {
        return dataStoreProvider.get().data.map { it.fatal_ble_security_exception_happens }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        error(e) { "Unhandled exception" }
        if (e is SecurityException && e.message?.startsWith(BLE_EXCEPTION_PREFIX) == true) {
            markFatalBleSecurityExceptionHappens()
        }
        previousExceptionHandler?.uncaughtException(t, e)
    }

    private fun markFatalBleSecurityExceptionHappens() {
        runBlocking {
            dataStoreProvider.get().updateData {
                it.copy(
                    fatal_ble_security_exception_happens = true
                )
            }
        }
    }
}
