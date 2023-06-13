package com.flipperdevices.faphub.search.impl.viewmodel

import com.flipperdevices.core.pager.OffsetAndLimitPagingSource
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.target.model.FlipperTarget

internal const val FAPS_PAGE_SIZE = 10

class FapsSearchPagingSource(
    private val fapNetworkApi: FapNetworkApi,
    private val searchRequest: String,
    private val target: FlipperTarget
) : OffsetAndLimitPagingSource<FapItemShort>(FAPS_PAGE_SIZE) {
    override val TAG = "FapsSearchPagingSource"
    override suspend fun load(offset: Int, limit: Int): List<FapItemShort> {
        return fapNetworkApi.search(
            target = target,
            query = searchRequest,
            offset = offset,
            limit = limit
        ).getOrThrow()
    }
}
