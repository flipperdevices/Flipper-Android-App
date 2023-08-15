package com.flipperdevices.bridge.dao.api

import com.flipperdevices.bridge.dao.api.model.FapHubHideItem
import kotlinx.coroutines.flow.Flow

interface FapHubHideItemApi {

    fun getHiddenItems(): Flow<List<FapHubHideItem>>
    suspend fun isHidden(applicationUid: String): Boolean
    suspend fun hideItem(applicationUid: String)
    suspend fun unHideItem(applicationUid: String)
}