package com.flipperdevices.core.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.verbose

private const val INITIAL_LOAD_SIZE = 0

abstract class OffsetAndLimitPagingSource<Value : Any>(
    private val pageSize: Int
) : PagingSource<Int, Value>(), LogTagProvider {
    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        return try {
            val position = params.key ?: INITIAL_LOAD_SIZE
            val offset = if (params.key != null) {
                position * pageSize
            } else {
                INITIAL_LOAD_SIZE
            }
            val faps = load(offset, params.loadSize)
            verbose { "Load ${faps.size} successful" }
            if (faps.isEmpty()) {
                LoadResult.Page(
                    data = faps,
                    prevKey = null,
                    nextKey = null
                )
            } else {
                val nextKey = position + (params.loadSize / pageSize)

                LoadResult.Page(
                    data = faps,
                    prevKey = null,
                    nextKey = nextKey
                )
            }
        } catch (exception: Throwable) {
            error(exception) { "Failed load $params" }
            LoadResult.Error(exception)
        }
    }

    abstract suspend fun load(offset: Int, limit: Int): List<Value>
}
