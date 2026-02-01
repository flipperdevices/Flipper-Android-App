package com.flipperdevices.bridge.connection.feature.networkproxy.impl.api

import android.content.Context
import com.flipperdevices.bridge.connection.feature.networkproxy.impl.service.NetworkProxyForegroundService
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.log.TaggedLogger
import com.flipperdevices.core.log.info

private val logger = TaggedLogger("NetworkProxyServiceController")

actual object NetworkProxyServiceController {
    @Volatile
    private var cachedAppContext: Context? = null

    private fun getContext(): Context? {
        // Try cached context first
        cachedAppContext?.let { return it }

        // Try to get from current activity and cache it
        val activity = CurrentActivityHolder.getCurrentActivity()
        if (activity != null) {
            cachedAppContext = activity.applicationContext
            return cachedAppContext
        }

        return null
    }

    actual fun startService() {
        val context = getContext()
        if (context == null) {
            logger.info { "Cannot start service: no context available" }
            return
        }
        logger.info { "Starting foreground service" }
        try {
            NetworkProxyForegroundService.start(context)
        } catch (e: Exception) {
            logger.info { "Failed to start service: ${e.message}" }
        }
    }

    actual fun stopService() {
        val context = getContext()
        if (context == null) {
            logger.info { "Cannot stop service: no context available" }
            return
        }
        logger.info { "Stopping foreground service" }
        try {
            NetworkProxyForegroundService.stop(context)
        } catch (e: Exception) {
            logger.info { "Failed to stop service: ${e.message}" }
        }
    }
}
