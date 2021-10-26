package com.flipperdevices.bridge.service.impl

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Context.BIND_IMPORTANT
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.flipperdevices.bridge.service.api.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FlipperServiceApi.Provider::class)
class FlipperServiceApiProviderImpl @Inject constructor(
    private val applicationContext: Context
) : FlipperServiceApi.Provider, ServiceConnection {
    private var serviceBinder: FlipperServiceBinder? = null
    private var isRequestedForBind: Boolean = false
    private val serviceConsumers = mutableListOf<WeakReference<FlipperBleServiceConsumer>>()

    @Synchronized
    override fun provideServiceApi(consumer: FlipperBleServiceConsumer) {
        serviceConsumers.add(WeakReference(consumer))
        invalidate()
        serviceBinder?.let { consumer.onServiceApiReady(it.serviceApi) }
    }

    @Synchronized
    override fun disconnect(consumer: FlipperBleServiceConsumer) {
        serviceConsumers.removeAll { it.get() == consumer }
        invalidate()
    }

    @Synchronized
    private fun invalidate() {
        // Remove empty consumers
        serviceConsumers.removeAll { it.get() == null }
        // If we not found any consumers, close ble connection and service
        if (serviceConsumers.isEmpty()) {
            stopServiceInternal()
            return
        }

        // If we have consumers and binder already exist, just do nothing
        if (serviceBinder != null) {
            return
        }

        // If we already request bind, just do nothing
        if (isRequestedForBind) {
            return
        }

        applicationContext.bindService(
            Intent(applicationContext, FlipperService::class.java), this,
            BIND_AUTO_CREATE or BIND_IMPORTANT
        )
        isRequestedForBind = true
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val flipperServiceBinder = service as FlipperServiceBinder
        serviceBinder = flipperServiceBinder
        isRequestedForBind = false
        invalidate()
        serviceConsumers.forEach {
            it.get()?.onServiceApiReady(flipperServiceBinder.serviceApi)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        resetInternal()
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        resetInternal()
    }

    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        resetInternal()
    }

    @Synchronized
    private fun stopServiceInternal() {
        serviceBinder?.closeService()
        serviceBinder = null
        applicationContext.unbindService(this)
        val stopIntent = Intent(applicationContext, FlipperService::class.java).apply {
            action = FlipperService.ACTION_STOP
        }
        applicationContext.startService(stopIntent)
    }

    @Synchronized
    private fun resetInternal() {
        serviceBinder = null
        isRequestedForBind = false
        invalidate()
    }
}
