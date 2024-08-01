package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.warn
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.network.helper.FapApplicationReceiveHelper
import com.flipperdevices.faphub.dao.network.network.api.FapNetworkApplicationApi
import com.flipperdevices.faphub.dao.network.network.model.FapNetworkHostEnum
import com.flipperdevices.faphub.dao.network.network.model.requests.KtorfitApplicationApiRequest
import com.flipperdevices.faphub.dao.network.network.utils.FapCachedCategoryApi
import com.flipperdevices.faphub.errors.api.throwable.FirmwareNotSupported
import com.flipperdevices.faphub.target.model.FlipperTarget
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapNetworkApi::class)
class FapNetworkApiImpl @Inject constructor(
    private val applicationApi: FapNetworkApplicationApi,
    private val categoryApi: FapCachedCategoryApi,
    private val networkHost: FapNetworkHostEnum,
    private val fapApplicationReceiveHelper: FapApplicationReceiveHelper
) : FapNetworkApi, LogTagProvider {
    override val TAG = "FapNetworkApi"

    override suspend fun getHostUrl() = networkHost.hostUrl
    override suspend fun getFeaturedItem(target: FlipperTarget) = catchWithDispatcher {
        debug { "Request featured item" }

        val response = applicationApi.getFeaturedApps(
            limit = 1
        )
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
        val response = fapApplicationReceiveHelper.get(
            target = target,
            category = category,
            sortType = sortType,
            offset = offset,
            limit = limit,
            applicationIds = applicationIds
        )
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
        val queryToServer = query.ifBlank {
            null
        }

        val response = when (target) {
            FlipperTarget.Unsupported -> throw FirmwareNotSupported()
            FlipperTarget.NotConnected -> applicationApi.getAll(
                KtorfitApplicationApiRequest(
                    limit = limit,
                    offset = offset,
                    query = queryToServer
                )
            )

            is FlipperTarget.Received -> applicationApi.getAll(
                KtorfitApplicationApiRequest(
                    limit = limit,
                    offset = offset,
                    query = queryToServer,
                    target = target.target,
                    sdkApiVersion = target.sdk.toString()
                )
            )
        }

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

        val response = when (target) {
            is FlipperTarget.Received -> applicationApi.get(
                id = id,
                target = target.target,
                sdkApiVersion = target.sdk.toString()
            )

            FlipperTarget.NotConnected,
            FlipperTarget.Unsupported -> applicationApi.get(
                id = id
            )
        }

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
    return@runCatching withContext(FlipperDispatchers.workStealingDispatcher) { block() }
}
