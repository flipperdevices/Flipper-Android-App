package com.flipperdevices.bridge.service.impl

import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.impl.di.FlipperBleServiceComponent
import com.flipperdevices.bridge.service.impl.di.FlipperServiceComponent
import com.flipperdevices.bridge.service.impl.notification.FLIPPER_NOTIFICATION_ID
import com.flipperdevices.bridge.service.impl.notification.FlipperNotificationHelper
import com.flipperdevices.bridge.service.impl.provider.error.CompositeFlipperServiceErrorListener
import com.flipperdevices.bridge.service.impl.provider.error.CompositeFlipperServiceErrorListenerImpl
import com.flipperdevices.bridge.service.impl.provider.lifecycle.FlipperServiceLifecycleListener
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.concurrent.atomic.AtomicBoolean

class FlipperService : LifecycleService(), LogTagProvider {
    override val TAG = "FlipperService-${hashCode()}"
    private val listener = CompositeFlipperServiceErrorListenerImpl()

    private val bleServiceComponent: FlipperBleServiceComponent by lazy {
        FlipperBleServiceComponent.ManualFactory.create(
            deps = ComponentHolder.component(),
            context = this,
            scope = lifecycleScope + FlipperDispatchers.workStealingDispatcher,
            serviceErrorListener = listener
        )
    }
    private val serviceApi by bleServiceComponent.serviceApiImpl
    private val binder by lazy { FlipperServiceBinder(serviceApi, listener) }
    private val stopped = AtomicBoolean(false)
    private var flipperNotification: FlipperNotificationHelper? = null

    override fun onCreate() {
        super.onCreate()
        info { "Start flipper service" }

        val component = ComponentHolder
            .component<FlipperServiceComponent>()

        val dataStoreSettings = component.dataStoreSettings.get()

        if (runBlockingWithLog { dataStoreSettings.data.first() }.used_foreground_service) {
            val flipperNotificationLocal = FlipperNotificationHelper(
                context = this,
                applicationParams = component.applicationParams
            )
            flipperNotification = flipperNotificationLocal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                PermissionHelper.getUngrantedPermission(
                    this,
                    PermissionHelper.getRequiredPermissions()
                ).isNotEmpty()
            ) {
                error { "Can't launch foreground service on Android API 34 and upper without bluetooth permission" }
                return
            }
            startForeground(FLIPPER_NOTIFICATION_ID, flipperNotificationLocal.show())
            flipperNotificationLocal.showStopButton()
        }

        serviceApi.internalInit()
    }

    override fun onBind(intent: Intent): Binder {
        super.onBind(intent)
        info { "On bind $intent" }
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        info { "Service receive command with action ${intent?.action}" }

        if (intent?.action == ACTION_STOP) {
            stopSelfInternal()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        info { "On destroy service" }
    }

    private fun stopSelfInternal() = lifecycleScope.launch {
        if (!stopped.compareAndSet(false, true)) {
            info { "Service already stopped" }
            return@launch
        }
        info { "Foreground stopped, try close service api" }
        serviceApi.close()
        info { "Service stop internal" }
        stopForeground(STOP_FOREGROUND_REMOVE)
        info { "Service api closed" }
        stopSelf()
        info { "Called stopSelf" }
        binder.listeners.removeAll { it.onInternalStop() }
    }

    companion object {
        const val ACTION_STOP = "com.flipperdevices.bridge.service.impl.FlipperService.STOP"
    }
}

class FlipperServiceBinder internal constructor(
    val serviceApi: FlipperServiceApi,
    compositeListener: CompositeFlipperServiceErrorListener,
    val listeners: MutableList<FlipperServiceLifecycleListener> = mutableListOf()
) : Binder(),
    CompositeFlipperServiceErrorListener by compositeListener
