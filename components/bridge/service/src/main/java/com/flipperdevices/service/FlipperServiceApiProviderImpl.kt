package com.flipperdevices.service

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Context.BIND_IMPORTANT
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import java.lang.ref.WeakReference

class FlipperServiceApiProviderImpl(
    private val context: Context
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
            serviceBinder?.closeService()
            serviceBinder = null
            context.unbindService(this)
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

        context.bindService(
            Intent(context, FlipperService::class.java), this,
            BIND_AUTO_CREATE or BIND_IMPORTANT
        )
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        TODO("Not yet implemented")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("Not yet implemented")
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
    }

    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
    }
}
