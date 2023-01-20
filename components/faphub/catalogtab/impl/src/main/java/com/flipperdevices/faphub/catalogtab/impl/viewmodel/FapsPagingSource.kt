package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.SortType

class FapsPagingSource(
    private val fapNetworkApi: FapNetworkApi,
    private val sortType: SortType
) : PagingSource<Int, FapItem>() {
    override fun getRefreshKey(state: PagingState<Int, FapItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FapItem> {
        return try {
            val nextPage = params.key ?: 1
            val faps = fapNetworkApi.getAllItem(sortType = sortType)
            LoadResult.Page(
                data = faps,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (exception: Throwable) {
            return LoadResult.Error(exception)
        }
    }
}
