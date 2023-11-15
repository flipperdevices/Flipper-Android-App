package com.flipperdevices.notification.api

import com.flipperdevices.notification.model.UpdateNotificationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FlipperAppNotificationApi {
    fun init()

    fun isSubscribedToUpdateNotificationTopic(scope: CoroutineScope): StateFlow<UpdateNotificationState>

    fun setSubscribeToUpdateAsync(
        isSubscribe: Boolean,
        scope: CoroutineScope,
        withNotificationSuccess: Boolean = false
    )
}
