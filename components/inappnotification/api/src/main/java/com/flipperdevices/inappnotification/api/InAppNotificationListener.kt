package com.flipperdevices.inappnotification.api

import com.flipperdevices.inappnotification.api.model.InAppNotification

@FunctionalInterface
interface InAppNotificationListener {
    fun onNewNotification(notification: InAppNotification)
}
