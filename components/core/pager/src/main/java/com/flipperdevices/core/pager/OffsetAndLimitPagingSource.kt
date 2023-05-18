package com.flipperdevices.core.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState

private const val INITIAL_LOAD_SIZE = 0

abstract class OffsetAndLimitPagingSource<Value : Any>(
    private val pageSize: Int
) : PagingSource<Int, Value>() {
    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        return try {
            val position = params.key ?: INITIAL_LOAD_SIZE
            val offset = if (params.key != null) {
                ((position - 1) * pageSize) + 1
            } else {
                INITIAL_LOAD_SIZE
            }
            val faps = load(offset, params.loadSize)
            val nextKey = position + (params.loadSize / pageSize)
            LoadResult.Page(
                data = faps,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (exception: Throwable) {
            return LoadResult.Error(exception)
        }
    }

    abstract suspend fun load(offset: Int, limit: Int): List<Value>
}
