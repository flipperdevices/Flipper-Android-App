package com.flipperdevices.faphub.dao.api

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType

interface FapNetworkApi {
    suspend fun getFeaturedItem(): FapItemShort
    suspend fun getAllItem(
        category: FapCategory? = null,
        sortType: SortType
    ): List<FapItem>

    suspend fun search(query: String): List<FapItem>
    suspend fun getCategories(): List<FapCategory>
    suspend fun getFapItemById(id: String): FapItem
}
