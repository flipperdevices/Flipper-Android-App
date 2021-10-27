package com.flipperdevices.bridge.service.impl.provider

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Context.BIND_IMPORTANT
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceError
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.service.impl.FlipperService
import com.flipperdevices.bridge.service.impl.FlipperServiceBinder
import com.flipperdevices.bridge.service.impl.provider.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.service.impl.utils.subscribeOnFirst
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FlipperServiceProvider::class)
class FlipperServiceProviderImpl @Inject constructor(
    private val applicationContext: Context
) : FlipperServiceProvider, ServiceConnection, FlipperServiceErrorListener {
    private var serviceBinder: FlipperServiceBinder? = null

    // true if we wait bind answer from android
    private var isRequestedForBind: Boolean = false
    private val serviceConsumers = mutableListOf<FlipperBleServiceConsumer>()

    @Synchronized
    override fun provideServiceApi(
        consumer: FlipperBleServiceConsumer,
        lifecycleOwner: LifecycleOwner,
        onDestroyEvent: Lifecycle.Event
    ) {
        serviceConsumers.add(consumer)
        lifecycleOwner.subscribeOnFirst(onDestroyEvent) { disconnectInternal(consumer) }

        invalidate()
        serviceBinder?.let { consumer.onServiceApiReady(it.serviceApi) }
    }

    @Synchronized
    override fun provideServiceApi(
        lifecycleOwner: LifecycleOwner,
        onDestroyEvent: Lifecycle.Event,
        onError: (FlipperBleServiceError) -> Unit,
        onBleManager: (FlipperServiceApi) -> Unit
    ) {
        val consumer = LambdaFlipperBleServiceConsumer(onBleManager, onError)
        provideServiceApi(consumer, lifecycleOwner, onDestroyEvent)
    }

    @Synchronized
    private fun invalidate() {
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

    private fun disconnectInternal(consumer: FlipperBleServiceConsumer) {
        serviceConsumers.remove(consumer)
        invalidate()
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val flipperServiceBinder = service as FlipperServiceBinder
        serviceBinder = flipperServiceBinder
        flipperServiceBinder.subscribe(this)
        isRequestedForBind = false
        invalidate()
        serviceConsumers.forEach { consumer ->
            consumer.onServiceApiReady(flipperServiceBinder.serviceApi)
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
        serviceBinder?.unsubscribe(this)
        serviceBinder = null
        applicationContext.unbindService(this)
        val stopIntent = Intent(applicationContext, FlipperService::class.java).apply {
            action = FlipperService.ACTION_STOP
        }
        applicationContext.startService(stopIntent)
    }

    @Synchronized
    private fun resetInternal() {
        serviceBinder?.unsubscribe(this)
        serviceBinder = null
        isRequestedForBind = false
        invalidate()
    }

    override fun onError(error: FlipperBleServiceError) {
        serviceConsumers.forEach { consumer ->
            consumer.onServiceBleError(error)
        }
    }
}
