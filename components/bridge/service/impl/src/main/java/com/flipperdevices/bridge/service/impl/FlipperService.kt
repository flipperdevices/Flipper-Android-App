package com.flipperdevices.bridge.service.impl

import android.content.Intent
import android.os.Binder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.impl.notification.FLIPPER_NOTIFICATION_ID
import com.flipperdevices.bridge.service.impl.notification.FlipperNotificationHelper
import com.flipperdevices.bridge.service.impl.provider.error.CompositeFlipperServiceErrorListener
import com.flipperdevices.bridge.service.impl.provider.error.CompositeFlipperServiceErrorListenerImpl
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.launch
import timber.log.Timber

class FlipperService : LifecycleService() {
    private val listener = CompositeFlipperServiceErrorListenerImpl()
    private val serviceApi by lazy { FlipperServiceApiImpl(this, this, listener) }
    private val binder by lazy { FlipperServiceBinder(serviceApi, listener) }
    private val stopped = AtomicBoolean(false)
    private lateinit var flipperNotification: FlipperNotificationHelper

    override fun onCreate() {
        super.onCreate()
        Timber.d("Start flipper service")

        flipperNotification = FlipperNotificationHelper(this)
        startForeground(FLIPPER_NOTIFICATION_ID, flipperNotification.show())
        if (!BuildConfig.INTERNAL) {
            flipperNotification.showStopButton()
        }

        serviceApi.internalInit()
    }

    override fun onBind(intent: Intent): Binder {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Service receive command with action ${intent?.action}")

        if (intent?.action == ACTION_STOP) {
            stopSelfInternal()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Destroy flipper service")
    }

    private fun stopSelfInternal() = lifecycleScope.launch {
        if (!stopped.compareAndSet(false, true)) {
            Timber.i("Service already stopped")
            return@launch
        }
        serviceApi.close()
        stopForeground(true)
        stopSelf()
    }

    companion object {
        const val ACTION_STOP = "com.flipperdevices.bridge.service.impl.FlipperService.STOP"
    }
}

class FlipperServiceBinder(
    val serviceApi: FlipperServiceApi,
    compositeListener: CompositeFlipperServiceErrorListener
) : Binder(), CompositeFlipperServiceErrorListener by compositeListener
