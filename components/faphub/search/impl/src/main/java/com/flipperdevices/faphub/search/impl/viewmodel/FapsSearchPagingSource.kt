package com.flipperdevices.faphub.search.impl.viewmodel

import com.flipperdevices.core.pager.OffsetAndLimitPagingSource
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItemShort

internal const val FAPS_PAGE_SIZE = 10

class FapsSearchPagingSource(
    private val fapNetworkApi: FapNetworkApi,
    private val searchRequest: String
) : OffsetAndLimitPagingSource<FapItemShort>(FAPS_PAGE_SIZE) {
    override val TAG = "FapsSearchPagingSource"
    override suspend fun load(offset: Int, limit: Int): List<FapItemShort> {
        return fapNetworkApi.search(searchRequest, offset, limit).getOrThrow()
    }
}
