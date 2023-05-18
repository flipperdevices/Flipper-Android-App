package com.flipperdevices.faphub.dao.api

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType

interface FapNetworkApi {
    suspend fun getFeaturedItem(): Result<FapItemShort>
    suspend fun getAllItem(
        category: FapCategory? = null,
        sortType: SortType,
        offset: Int,
        limit: Int
    ): Result<List<FapItemShort>>

    suspend fun search(
        query: String,
        offset: Int,
        limit: Int
    ): Result<List<FapItemShort>>

    suspend fun getCategories(): Result<List<FapCategory>>
    suspend fun getFapItemById(id: String): Result<FapItem>
}
