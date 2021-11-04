package com.flipperdevices.bridge.service.impl.provider.lifecycle

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.flipperdevices.bridge.service.impl.FlipperService
import com.flipperdevices.bridge.service.impl.FlipperServiceBinder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info

class FlipperServiceConnectionHelperImpl(
    private val applicationContext: Context,
    private val onBind: (FlipperServiceBinder) -> Unit,
    private val onUnbind: () -> Unit
) : ServiceConnection,
    FlipperServiceLifecycleListener,
    FlipperServiceConnectionHelper,
    LogTagProvider {
    override val TAG = "FlipperServiceConnectionHolder"
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
        onServiceUnbindInternal()
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        info { "Binding died for service $name" }
        onServiceUnbindInternal()
    }

    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        info { "Null binding for service $name" }
        onServiceUnbindInternal()
    }

    override fun onInternalStop(): Boolean {
        info { "Service notified that it internal stop self" }
        onServiceUnbindInternal()
        return true
    }

    override fun connect() {
        info { "#connect" }
        // If we already request bind, just do nothing
        if (isRequestedForBind) {
            info { "Already request bind, skip invalidate" }
            return
        }

        // Without it service destroy after unbind
        val componentName =
            applicationContext.startService(Intent(applicationContext, FlipperService::class.java))
        val bindSuccessful = applicationContext.bindService(
            Intent(applicationContext, FlipperService::class.java), this,
            Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
        )
        isRequestedForBind = true
        info { "Start service. bindSuccessful is $bindSuccessful, componentName is $componentName" }
    }

    override fun disconnect() {
        info { "#disconnect" }
        if (serviceBinder != null || isRequestedForBind) {
            applicationContext.unbindService(this)
        }
        serviceBinder = null
        isRequestedForBind = false
    }

    private fun onServiceUnbindInternal() {
        info { "#onServiceUnbindInternal" }
        isRequestedForBind = false
        onUnbind()
    }
}
