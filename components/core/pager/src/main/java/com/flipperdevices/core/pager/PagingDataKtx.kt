package com.flipperdevices.core.pager

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
