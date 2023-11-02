package com.flipperdevices.notification.api

import com.flipperdevices.notification.model.UpdateNotificationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FlipperAppNotificationApi {
    fun init()

    fun isSubscribedToUpdateNotificationTopic(scope: CoroutineScope): StateFlow<UpdateNotificationState>

    suspend fun setSubscribeToUpdate(isSubscribe: Boolean)
}