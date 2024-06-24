package com.flipperdevices.toolstab.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface ToolsApi {
    fun hasNotification(scope: CoroutineScope): Flow<Boolean>
}
