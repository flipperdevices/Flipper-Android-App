package com.flipperdevices.hub.api

import kotlinx.coroutines.flow.Flow

interface HubApi {
    fun hasNotification(): Flow<Boolean>
}
