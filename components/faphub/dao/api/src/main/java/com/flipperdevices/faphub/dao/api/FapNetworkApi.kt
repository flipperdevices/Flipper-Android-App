package com.flipperdevices.faphub.dao.api

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.SortType

interface FapNetworkApi {
    suspend fun getFeaturedItem(): FapItem
    suspend fun getAllItem(sortType: SortType): List<FapItem>
    suspend fun search(query: String): List<FapItem>
    suspend fun getCategories(): List<FapCategory>
}
