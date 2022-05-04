package com.flipperdevices.bridge.service.impl.provider

import android.content.Context
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.service.impl.BuildConfig
import com.flipperdevices.bridge.service.impl.FlipperServiceBinder
import com.flipperdevices.bridge.service.impl.provider.lifecycle.FlipperServiceConnectionHelper
import com.flipperdevices.bridge.service.impl.provider.lifecycle.FlipperServiceConnectionHelperImpl
import com.flipperdevices.bridge.service.impl.utils.subscribeOnFirst
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FlipperServiceProvider::class)
class FlipperServiceProviderImpl @Inject constructor(
    applicationContext: Context,
    dataStoreSettings: DataStore<Settings>
) : FlipperServiceProvider,
    FlipperServiceErrorListener,
    LogTagProvider {
    override val TAG = "FlipperServiceProvider"
    private val connectionHelper: FlipperServiceConnectionHelper =
        FlipperServiceConnectionHelperImpl(
            applicationContext,
            dataStoreSettings,
            onBind = this::onServiceBind,
            onUnbind = this::onServiceUnbind
        )

    private val serviceConsumers = mutableListOf<FlipperBleServiceConsumer>()

    @Synchronized
    override fun provideServiceApi(
        consumer: FlipperBleServiceConsumer,
        lifecycleOwner: LifecycleOwner,
        onDestroyEvent: Lifecycle.Event
    ) {
        if (BuildConfig.INTERNAL && Looper.myLooper() != Looper.getMainLooper()) {
            error("provideServiceApi() must be called from the main thread")
        }

        info { "Add new consumer: $consumer (${consumer.hashCode()})" }
        serviceConsumers.add(consumer)
        lifecycleOwner.subscribeOnFirst(onDestroyEvent) { disconnectInternal(consumer) }

        invalidate()
        connectionHelper.serviceBinder?.let {
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
        if (BuildConfig.INTERNAL && Looper.myLooper() != Looper.getMainLooper()) {
            error("provideServiceApi() must be called from the main thread")
        }

        val consumer = LambdaFlipperBleServiceConsumer(onBleManager, onError)
        provideServiceApi(consumer, lifecycleOwner, onDestroyEvent)
    }

    @Synchronized
    private fun invalidate() {
        info { "Invalidate service provider storage. Current size: ${serviceConsumers.size}" }
        // If we not found any consumers, close ble connection and service
        if (serviceConsumers.isEmpty()) {
            info { "Service consumers is empty, stop service" }
            @Suppress("ForbiddenComment")
            // TODO: Add timeout
            // stopServiceInternal()
            return
        }

        // If we have consumers and binder already exist, just do nothing
        if (connectionHelper.serviceBinder != null) {
            info { "Already find binder, skip invalidate" }
            return
        }

        connectionHelper.connect()
    }

    private fun disconnectInternal(consumer: FlipperBleServiceConsumer) {
        info { "Remove consumer $consumer (${consumer.hashCode()})" }
        serviceConsumers.remove(consumer)
        invalidate()
    }

    @Synchronized
    @Suppress("UnusedPrivateMember")
    private fun stopServiceInternal() {
        info { "Internal stop service" }
        resetInternalWithoutInvalidate()
    }

    private fun onServiceBind(binder: FlipperServiceBinder) {
        binder.subscribe(this)
        invalidate()
        serviceConsumers.forEach { consumer ->
            consumer.onServiceApiReady(binder.serviceApi)
        }
    }

    private fun onServiceUnbind() {
        info { "Reset binder with invalidate" }
        resetInternalWithoutInvalidate()
        invalidate()
    }

    private fun resetInternalWithoutInvalidate() {
        info { "Reset binder internal, unsubscribe" }
        connectionHelper.disconnect()
        connectionHelper.serviceBinder?.unsubscribe(this)
    }

    override fun onError(error: FlipperBleServiceError) {
        error { "Service return error $error (${error.ordinal})" }
        serviceConsumers.forEach { consumer ->
            consumer.onServiceBleError(error)
        }
    }
}
