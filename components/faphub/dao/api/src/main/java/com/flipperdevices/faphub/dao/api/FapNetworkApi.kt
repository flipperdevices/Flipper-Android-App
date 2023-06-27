package com.flipperdevices.faphub.dao.api

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.target.model.FlipperTarget

interface FapNetworkApi {
    suspend fun getHostUrl(): String
    suspend fun getFeaturedItem(
        target: FlipperTarget
    ): Result<FapItemShort>

    @Suppress("LongParameterList")
    suspend fun getAllItem(
        target: FlipperTarget,
        category: FapCategory? = null,
        sortType: SortType,
        offset: Int,
        limit: Int,
        applicationIds: List<String>? = null
    ): Result<List<FapItemShort>>

    suspend fun search(
        target: FlipperTarget,
        query: String,
        offset: Int,
        limit: Int
    ): Result<List<FapItemShort>>

    suspend fun getCategories(target: FlipperTarget): Result<List<FapCategory>>
    suspend fun getFapItemById(target: FlipperTarget, id: String): Result<FapItem>
}
