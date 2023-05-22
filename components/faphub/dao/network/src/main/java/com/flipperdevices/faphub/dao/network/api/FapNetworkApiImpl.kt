package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitApplicationApi
import com.flipperdevices.faphub.dao.network.retrofit.model.types.ApplicationSortType
import com.flipperdevices.faphub.dao.network.retrofit.model.types.SortOrderType
import com.flipperdevices.faphub.dao.network.retrofit.utils.FapHubNetworkCategoryApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class, FapNetworkApi::class)
class FapNetworkApiImpl @Inject constructor(
    private val applicationApi: RetrofitApplicationApi,
    private val categoryApi: FapHubNetworkCategoryApi
) : FapNetworkApi, LogTagProvider {
    override val TAG = "FapNetworkApi"
    override suspend fun getFeaturedItem() = catchWithDispatcher {
        debug { "Request featured item" }

        val response = applicationApi.getFeaturedApps(limit = 1)
        debug { "Provider response: $response" }

        val responseItem = response.firstOrNull() ?: error("Empty response")
        val fapCategory = categoryApi.get(responseItem.categoryId)

        return@catchWithDispatcher responseItem.toFapItemShort(fapCategory).also {
            debug { "Provider feature item: $it" }
        }
    }

    override suspend fun getAllItem(
        category: FapCategory?,
        sortType: SortType,
        offset: Int,
        limit: Int
    ) = catchWithDispatcher {
        debug { "Request all item" }
        val response = applicationApi.getAll(
            offset = offset,
            limit = limit,
            sortBy = ApplicationSortType.fromSortType(sortType),
            sortOrder = SortOrderType.fromSortType(sortType)
        )
        debug { "Provider response: $response" }

        val fapItems = response.pmap {
            it.toFapItemShort(categoryApi.get(it.categoryId))
        }.also {
            debug { "Provider all item: $it" }
        }

        return@catchWithDispatcher fapItems
    }

    override suspend fun search(
        query: String,
        offset: Int,
        limit: Int
    ) = getAllItem(
        sortType = SortType.NAME_DESC,
        offset = offset,
        limit = limit
    )

    override suspend fun getCategories() = catchWithDispatcher {
        debug { "Request categories" }

        val response = categoryApi.getAll()
        debug { "Provider response: $response" }

        return@catchWithDispatcher response.map { it.toFapCategory() }.also {
            debug { "Provider categories: $it" }
        }
    }

    override suspend fun getFapItemById(id: String) = catchWithDispatcher {
        debug { "Request fap item by id $id" }

        val response = applicationApi.get(id)
        debug { "Provider response: $response" }

        return@catchWithDispatcher response.toFapItem(categoryApi.get(response.categoryId))
    }
}

private suspend fun <T> catchWithDispatcher(
    block: suspend () -> T
): Result<T> = runCatching {
    return@runCatching withContext(Dispatchers.IO) { block() }
}
