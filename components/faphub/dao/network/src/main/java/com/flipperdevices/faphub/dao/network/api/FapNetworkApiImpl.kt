package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.network.model.ApplicationDetailed
import com.flipperdevices.faphub.dao.network.model.ApplicationDetailed.Companion.toFapItem
import com.flipperdevices.faphub.dao.network.model.ApplicationShort
import com.flipperdevices.faphub.dao.network.model.ApplicationShort.Companion.toFapItemShort
import com.flipperdevices.faphub.dao.network.model.Category
import com.flipperdevices.faphub.dao.network.model.Category.Companion.toFapCategory
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val FAP_URL = "https://catalog.flipp.dev/api/v0"

@ContributesBinding(AppGraph::class, FapNetworkApi::class)
class FapNetworkApiImpl @Inject constructor(
    private val client: HttpClient,
) : FapNetworkApi, LogTagProvider {
    override val TAG = "FapNetworkApi"
    override suspend fun getFeaturedItem(): FapItemShort = withContext(Dispatchers.IO) {
        debug { "Request featured item" }

        val response = client.get(
            urlString = "$FAP_URL/application/featured?limit=1"
        ).body<Array<ApplicationShort>>()
        debug { "Provider response: $response" }

        val responseItem = response.firstOrNull() ?: error("Empty response")
        val fapCategory = getCategoryById(responseItem.categoryId)

        return@withContext responseItem.toFapItemShort(fapCategory).also {
            debug { "Provider feature item: $it" }
        }
    }

    override suspend fun getAllItem(
        category: FapCategory?,
        sortType: SortType
    ): List<FapItem> = withContext(Dispatchers.IO) {
        debug { "Request all item" }

        val response = client.get(
            urlString = "$FAP_URL/application"
        ).body<Array<ApplicationDetailed>>()
        debug { "Provider response: $response" }

        val fapItems = response.map {
            val fapCategory = getCategoryById(it.categoryId)
            it.toFapItem(fapCategory)
        }.also {
            debug { "Provider all item: $it" }
        }

        return@withContext fapItems
    }

    override suspend fun search(query: String) = getAllItem(sortType = SortType.UPDATED)

    override suspend fun getCategories(): List<FapCategory> = withContext(Dispatchers.IO) {
        debug { "Request categories" }

        val response = client.get(
            urlString = "$FAP_URL/category"
        ).body<Array<Category>>()
        debug { "Provider response: $response" }

        return@withContext response.map { it.toFapCategory() }.also {
            debug { "Provider categories: $it" }
        }
    }

    override suspend fun getFapItemById(id: String): FapItem = withContext(Dispatchers.IO) {
        debug { "Request fap item by id $id" }

        val response = client.get(
            urlString = "$FAP_URL/application/$id"
        ).body<ApplicationDetailed>()
        debug { "Provider response: $response" }

        val fapCategory = getCategoryById(response.categoryId)

        return@withContext response.toFapItem(fapCategory)
    }

    private suspend fun getCategoryById(id: String): FapCategory = withContext(Dispatchers.IO) {
        debug { "Request category by id $id" }

        val response = client.get(
            urlString = "$FAP_URL/category/$id"
        ).body<Category>()
        debug { "Provider response: $response" }

        return@withContext response.toFapCategory().also {
            debug { "Provider category by $id: $it" }
        }
    }
}
