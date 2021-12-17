package com.flipperdevices.bridge.dao.api

import com.flipperdevices.bridge.dao.api.delegates.KeyApi

interface DaoApi {
    suspend fun getKeysApi(): KeyApi
}
