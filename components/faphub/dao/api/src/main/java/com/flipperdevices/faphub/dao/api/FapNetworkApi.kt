package com.flipperdevices.faphub.dao.api

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem

interface FapNetworkApi {
    suspend fun getFeaturedItem(): FapItem
    suspend fun getAllItem(): List<FapItem>
    suspend fun getCategories(): List<FapCategory>
}
