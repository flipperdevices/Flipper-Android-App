package com.flipperdevices.hub.api

import com.github.terrakok.cicerone.Screen
import kotlinx.coroutines.flow.Flow

interface HubApi {
    fun hasNotification(): Flow<Boolean>
    fun getHubScreen(): Screen
}
