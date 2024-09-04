package com.flipperdevices.bridge.service.impl.provider.lifecycle

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.impl.FlipperService
import com.flipperdevices.bridge.service.impl.FlipperServiceBinder
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class FlipperServiceConnectionHelperImpl(
    private val applicationContext: Context,
    private val dataStoreSettings: DataStore<Settings>,
    private val onBind: (FlipperServiceBinder) -> Unit,
    private val onUnbind: () -> Unit
) : ServiceConnection,
    FlipperServiceLifecycleListener,
    FlipperServiceConnectionHelper,
    LogTagProvider {
    override val TAG = "FlipperServiceConnectionHelper"
    override var serviceBinder: FlipperServiceBinder? = null
        private set

    // true if we wait bind answer from android
    private var isRequestedForBind: Boolean = false

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        info { "Service $name connected" }
        isRequestedForBind = false
        val flipperServiceBinder = service as FlipperServiceBinder
        serviceBinder = flipperServiceBinder
        flipperServiceBinder.listeners.add(this)

        onBind(flipperServiceBinder)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        info { "Service $name disconnected" }
        serviceBinder = null
        onServiceUnboundedInternal()
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        info { "Binding died for service $name" }
        serviceBinder = null
        onServiceUnboundedInternal()
    }

    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        info { "Null binding for service $name" }
        serviceBinder = null
        onServiceUnboundedInternal()
    }

    override fun onInternalStop(): Boolean {
        info { "Service notified that it internal stop self" }
        onUnbind()
        return true
    }

    @Synchronized
    override fun connect() {
        info { "#connect" }
        // If we already request bind, just do nothing
        if (isRequestedForBind) {
            info { "Already request bind, skip invalidate" }
            return
        }
        val usedBackgroundService = runBlockingWithLog {
            dataStoreSettings.data.first()
        }.used_foreground_service

        if (usedBackgroundService) {
            // Without it service destroy after unbind
            applicationContext.startService(Intent(applicationContext, FlipperService::class.java))
        }
        val bindSuccessful = applicationContext.bindService(
            Intent(applicationContext, FlipperService::class.java),
            this,
            Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
        )
        isRequestedForBind = true
        info { "Start service. bindSuccessful is $bindSuccessful" }
    }

    @Synchronized
    override fun disconnect() {
        info { "#disconnect" }
        val serviceRunning = serviceBinder != null || isRequestedForBind
        if (serviceRunning) {
            val usedForegroundService = runBlocking {
                dataStoreSettings.data.first()
            }.used_foreground_service
            if (usedForegroundService) {
                info { "Stop service" }
                val stopIntent = Intent(applicationContext, FlipperService::class.java).apply {
                    action = FlipperService.ACTION_STOP
                }
                applicationContext.startService(stopIntent)
            }

            applicationContext.unbindService(this)
        }
        serviceBinder = null
        isRequestedForBind = false
    }

    @Synchronized
    private fun onServiceUnboundedInternal() {
        info { "#onServiceUnbindInternal" }
        serviceBinder = null
        isRequestedForBind = false
        onUnbind()
    }
}
