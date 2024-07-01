package com.flipperdevices.core.pager

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun <T : Any> loadingPagingDataFlow(): Flow<PagingData<T>> {
    return flowOf(
        PagingData.from(
            emptyList(),
            LoadStates(
                append = LoadState.NotLoading(endOfPaginationReached = false),
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(endOfPaginationReached = false)
            )
        )
    )
}

fun <T : Any, K> Flow<PagingData<T>>.distinctBy(selector: (T) -> K): Flow<PagingData<T>> {
    val set = HashSet<K>()
    return map { pageData ->
        pageData.filter { element ->
            set.add(selector(element))
        }
    }
}
