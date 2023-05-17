package com.flipperdevices.faphub.category.impl.viewmodel

import com.flipperdevices.core.pager.OffsetAndLimitPagingSource
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType

internal const val FAPS_PAGE_SIZE = 100

class FapsCategoryPagingSource(
    private val fapNetworkApi: FapNetworkApi,
    private val fapCategory: FapCategory,
    private val sortType: SortType
) : OffsetAndLimitPagingSource<FapItemShort>(FAPS_PAGE_SIZE) {

    override suspend fun load(offset: Int, limit: Int): List<FapItemShort> {
        return fapNetworkApi.getAllItem(fapCategory, sortType, offset, limit)
    }
}
