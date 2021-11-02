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
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FlipperServiceProvider::class)
@Suppress("TooManyFunctions")
class FlipperServiceProviderImpl @Inject constructor(
    private val applicationContext: Context
) : FlipperServiceProvider, ServiceConnection, FlipperServiceErrorListener, LogTagProvider {
    override val TAG = "FlipperServiceProvider"
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
        info { "Add new consumer: $consumer (${consumer.hashCode()})" }
        serviceConsumers.add(consumer)
        lifecycleOwner.subscribeOnFirst(onDestroyEvent) { disconnectInternal(consumer) }

        invalidate()
        serviceBinder?.let {
            info { "Found binder object, notify consumer now" }
            consumer.onServiceApiReady(it.serviceApi)
        }
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
        info { "Invalidate service provider storage. Current size: ${serviceConsumers.size}" }
        // If we not found any consumers, close ble connection and service
        if (serviceConsumers.isEmpty()) {
            info { "Service consumers is empty, stop service" }
            stopServiceInternal()
            return
        }

        // If we have consumers and binder already exist, just do nothing
        if (serviceBinder != null) {
            info { "Already find binder, skip invalidate" }
            return
        }

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
            BIND_AUTO_CREATE or BIND_IMPORTANT
        )
        isRequestedForBind = true
        info { "Start service. bindSuccessful is $bindSuccessful, componentName is $componentName" }
    }

    private fun disconnectInternal(consumer: FlipperBleServiceConsumer) {
        info { "Remove consumer $consumer (${consumer.hashCode()})" }
        serviceConsumers.remove(consumer)
        invalidate()
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        info { "Service $name connected" }
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
        info { "Service $name disconnected" }
        resetInternal()
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        info { "Binding died for service $name" }
        resetInternal()
    }

    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        info { "Null binding for service $name" }
        resetInternal()
    }

    @Synchronized
    private fun stopServiceInternal() {
        info { "Internal stop service" }
        resetInternalWithoutInvalidate()

        val stopIntent = Intent(applicationContext, FlipperService::class.java).apply {
            action = FlipperService.ACTION_STOP
        }
        applicationContext.startService(stopIntent)
    }

    @Synchronized
    private fun resetInternal() {
        info { "Reset binder with invalidate" }
        resetInternalWithoutInvalidate()
        invalidate()
    }

    private fun resetInternalWithoutInvalidate() {
        info { "Reset binder internal, unsubscribe" }
        applicationContext.unbindService(this)
        serviceBinder?.unsubscribe(this)
        serviceBinder = null
        isRequestedForBind = false
    }

    override fun onError(error: FlipperBleServiceError) {
        error { "Service return error $error (${error.ordinal})" }
        serviceConsumers.forEach { consumer ->
            consumer.onServiceBleError(error)
        }
    }
}
