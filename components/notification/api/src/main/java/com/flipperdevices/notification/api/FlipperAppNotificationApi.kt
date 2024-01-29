package com.flipperdevices.notification.api

import com.flipperdevices.notification.model.UpdateNotificationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface FlipperAppNotificationApi {
    fun init()

    fun isSubscribedToUpdateNotificationTopic(scope: CoroutineScope): Flow<UpdateNotificationState>

    fun setSubscribeToUpdateAsync(
        isSubscribe: Boolean,
        scope: CoroutineScope,
        withNotificationSuccess: Boolean = false
    )
}
