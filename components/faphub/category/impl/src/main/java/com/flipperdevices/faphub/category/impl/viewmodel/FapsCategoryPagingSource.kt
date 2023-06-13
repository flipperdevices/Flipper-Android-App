package com.flipperdevices.faphub.category.impl.viewmodel

import com.flipperdevices.core.pager.OffsetAndLimitPagingSource
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.target.model.FlipperTarget

internal const val FAPS_PAGE_SIZE = 10

class FapsCategoryPagingSource(
    private val fapNetworkApi: FapNetworkApi,
    private val fapCategory: FapCategory,
    private val sortType: SortType,
    private val target: FlipperTarget
) : OffsetAndLimitPagingSource<FapItemShort>(FAPS_PAGE_SIZE) {
    override val TAG = "FapsCategoryPagingSource"

    override suspend fun load(offset: Int, limit: Int): List<FapItemShort> {
        return fapNetworkApi.getAllItem(
            target = target,
            category = fapCategory,
            sortType = sortType,
            offset = offset,
            limit = limit
        ).getOrThrow()
    }
}
