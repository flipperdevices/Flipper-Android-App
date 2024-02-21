package com.flipperdevices.faphub.installedtab.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface FapUpdatePendingCountApi {
    fun getUpdatePendingCount(): Flow<Int>
}