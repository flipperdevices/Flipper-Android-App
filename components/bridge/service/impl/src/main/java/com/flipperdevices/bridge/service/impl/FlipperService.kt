package com.flipperdevices.bridge.service.impl

import android.content.Intent
import android.os.Binder
import androidx.lifecycle.LifecycleService
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.impl.notification.FLIPPER_NOTIFICATION_ID
import com.flipperdevices.bridge.service.impl.notification.FlipperNotificationHelper
import timber.log.Timber

class FlipperService : LifecycleService(), FlipperServiceApi {
    private val binder = FlipperServiceBinder(this)
    private lateinit var flipperNotification: FlipperNotificationHelper

    override fun onCreate() {
        super.onCreate()
        Timber.d("Start flipper service")

        flipperNotification = FlipperNotificationHelper(this)
        startForeground(FLIPPER_NOTIFICATION_ID, flipperNotification.show())
        flipperNotification.showStopButton()
    }

    override fun onBind(intent: Intent): Binder {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("FlipperService#onStartCommand")

        if (intent?.action == ACTION_STOP) {
            stopSelfInternal()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("FlipperService#onDestroy")
    }

    private fun stopSelfInternal() {
        stopForeground(true)
        stopSelf()
    }

    override fun getRequestApi(): FlipperRequestApi {
        TODO("Not yet implemented")
    }

    companion object {
        const val ACTION_STOP = "com.flipperdevices.bridge.service.impl.FlipperService.STOP"
    }
}

class FlipperServiceBinder(
    val serviceApi: FlipperServiceApi
) : Binder() {
    fun closeService() {
        Timber.i("FlipperService#closeService")
    }
}
