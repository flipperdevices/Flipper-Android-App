package com.flipperdevices.nfc.attack.api

import kotlinx.coroutines.flow.Flow

interface NfcAttackApi {
    fun notificationCount(): Flow<Int>
}
