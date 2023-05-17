package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import com.flipperdevices.core.pager.OffsetAndLimitPagingSource
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType

internal const val FAPS_PAGE_SIZE = 100

class FapsPagingSource(
    private val fapNetworkApi: FapNetworkApi,
    private val sortType: SortType
) : OffsetAndLimitPagingSource<FapItemShort>(FAPS_PAGE_SIZE) {
    override suspend fun load(offset: Int, limit: Int): List<FapItemShort> {
        return fapNetworkApi.getAllItem(
            sortType = sortType,
            offset = offset,
            limit = limit
        )
    }
}
