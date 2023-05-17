package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitApplicationApi
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
    override suspend fun getFeaturedItem(): FapItemShort = withContext(Dispatchers.IO) {
        debug { "Request featured item" }

        val response = applicationApi.getFeaturedApps(limit = 1)
        debug { "Provider response: $response" }

        val responseItem = response.firstOrNull() ?: error("Empty response")
        val fapCategory = categoryApi.get(responseItem.categoryId)

        return@withContext responseItem.toFapItemShort(fapCategory).also {
            debug { "Provider feature item: $it" }
        }
    }

    override suspend fun getAllItem(
        category: FapCategory?,
        sortType: SortType,
        offset: Int,
        limit: Int
    ): List<FapItemShort> = withContext(Dispatchers.IO) {
        debug { "Request all item" }

        val response = applicationApi.getAll(
            offset = offset,
            limit = limit
        )
        debug { "Provider response: $response" }

        val fapItems = response.pmap {
            it.toFapItemShort(categoryApi.get(it.categoryId))
        }.also {
            debug { "Provider all item: $it" }
        }

        return@withContext fapItems
    }

    override suspend fun search(
        query: String,
        offset: Int,
        limit: Int
    ) = getAllItem(
        sortType = SortType.UPDATED,
        offset = offset,
        limit = limit
    )

    override suspend fun getCategories(): List<FapCategory> = withContext(Dispatchers.IO) {
        debug { "Request categories" }

        val response = categoryApi.getAll()
        debug { "Provider response: $response" }

        return@withContext response.map { it.toFapCategory() }.also {
            debug { "Provider categories: $it" }
        }
    }

    override suspend fun getFapItemById(id: String): FapItem = withContext(Dispatchers.IO) {
        debug { "Request fap item by id $id" }

        val response = applicationApi.get(id)
        debug { "Provider response: $response" }

        return@withContext response.toFapItem(categoryApi.get(response.categoryId))
    }
}
