package com.flipperdevices.hub.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface HubApi {
    fun hasNotification(scope: CoroutineScope): Flow<Boolean>
}
