package com.flipperdevices.inappnotification.api

import com.flipperdevices.inappnotification.api.model.InAppNotification

interface InAppNotificationStorage {
    fun subscribe(listener: InAppNotificationListener)
    fun unsubscribe()
    suspend fun addNotification(notification: InAppNotification)
}
