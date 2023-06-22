package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.warn
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitApplicationApi
import com.flipperdevices.faphub.dao.network.retrofit.model.types.ApplicationSortType
import com.flipperdevices.faphub.dao.network.retrofit.model.types.SortOrderType
import com.flipperdevices.faphub.dao.network.retrofit.utils.FapHubNetworkCategoryApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class, FapNetworkApi::class)
class FapNetworkApiImpl @Inject constructor(
    private val applicationApi: KtorfitApplicationApi,
    private val categoryApi: FapHubNetworkCategoryApi
) : FapNetworkApi, LogTagProvider {
    override val TAG = "FapNetworkApi"
    override suspend fun getFeaturedItem(target: FlipperTarget) = catchWithDispatcher {
        debug { "Request featured item" }

        val response = applicationApi.getFeaturedApps()
        debug { "Provider response: $response" }

        val responseItem = response.firstOrNull() ?: error("Empty response")
        val fapCategory = categoryApi.get(target, responseItem.categoryId)

        val item = responseItem.toFapItemShort(fapCategory, target)
        debug { "Provider feature item: $item" }

        return@catchWithDispatcher item ?: error("Fap item is empty")
    }

    override suspend fun getAllItem(
        target: FlipperTarget,
        category: FapCategory?,
        sortType: SortType,
        offset: Int,
        limit: Int,
        applicationIds: List<String>?
    ) = catchWithDispatcher {
        if (limit == 0) {
            warn { "Receive limit as zero, so just return empty request" }
            return@catchWithDispatcher emptyList()
        }
        debug { "Request all item" }
        val response = when (target) {
            FlipperTarget.Unsupported,
            FlipperTarget.NotConnected -> applicationApi.getAll(
                offset = offset,
                limit = limit,
                sortBy = ApplicationSortType.fromSortType(sortType),
                sortOrder = SortOrderType.fromSortType(sortType),
                categoryId = category?.id,
                applications = applicationIds
            )

            is FlipperTarget.Received -> applicationApi.getAllWithTarget(
                offset = offset,
                limit = limit,
                sortBy = ApplicationSortType.fromSortType(sortType),
                sortOrder = SortOrderType.fromSortType(sortType),
                target = target.target,
                sdkApiVersion = target.sdk.toString(),
                categoryId = category?.id,
                applications = applicationIds
            )
        }
        debug { "Provider response: $response" }

        val fapItems = response.mapNotNull {
            it.toFapItemShort(categoryApi.get(target, it.categoryId), target)
        }.also {
            debug { "Provider all item: $it" }
        }

        return@catchWithDispatcher fapItems
    }

    override suspend fun search(
        target: FlipperTarget,
        query: String,
        offset: Int,
        limit: Int
    ) = catchWithDispatcher {
        val response = applicationApi.getAll(
            limit = limit,
            offset = offset,
            query = query
        )

        val fapItems = response.mapNotNull {
            it.toFapItemShort(categoryApi.get(target, it.categoryId), target)
        }.also {
            debug { "Provider all item: $it" }
        }

        return@catchWithDispatcher fapItems
    }

    override suspend fun getCategories(target: FlipperTarget) = catchWithDispatcher {
        debug { "Request categories" }

        val response = categoryApi.getAll(target)
        debug { "Provider response: $response" }

        return@catchWithDispatcher response.map { it.toFapCategory() }.also {
            debug { "Provider categories: $it" }
        }
    }

    override suspend fun getFapItemById(target: FlipperTarget, id: String) = catchWithDispatcher {
        debug { "Request fap item by id $id" }

        val response = applicationApi.get(id)
        debug { "Provider response: $response" }
        val category = categoryApi.get(target, response.categoryId)
            ?: error("Category can't be empty")
        debug { "Provided category: $category" }

        return@catchWithDispatcher response.toFapItem(category, target)
    }
}

private suspend fun <T> catchWithDispatcher(
    block: suspend () -> T
): Result<T> = runCatching {
    return@runCatching withContext(Dispatchers.IO) { block() }
}
