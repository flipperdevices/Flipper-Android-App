package com.flipperdevices.faphub.dao.network.retrofit.utils

import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.verbose
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitCategoryApi
import com.flipperdevices.faphub.dao.network.retrofit.model.RetrofitCategory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex

class FapHubNetworkCategoryApi(
    private val categoryApi: RetrofitCategoryApi
) : LogTagProvider {
    override val TAG = "FapHubNetworkCategoryApi"

    private val mutex = Mutex()
    private val categories = mutableMapOf<String, CompletableDeferred<RetrofitCategory>>()
    suspend fun get(id: String): FapCategory {
        val (deferred, needRequest) = withLockResult(mutex, "get") {
            val category = categories[id]
            if (category != null) {
                return@withLockResult category to false
            }
            return@withLockResult CompletableDeferred<RetrofitCategory>().also {
                categories[id] = it
            } to true
        }

        if (needRequest) {
            try {
                val category = categoryApi.get(id)
                verbose { "[CACHE MISS] Request category $id" }
                deferred.complete(category)
            } catch (throwable: Throwable) {
                error(throwable) { "Failed get category" }
                deferred.completeExceptionally(throwable)
                withLockResult(mutex, "remove") {
                    categories.remove(id)
                }
            }
        } else {
            verbose { "[CACHE HIT] Request category $id" }
        }

        return deferred.await().toFapCategory()
    }

    suspend fun getAll(): List<RetrofitCategory> {
        val fetchedCategories = categoryApi.getAll()
        withLockResult(mutex, "batch_update") {
            fetchedCategories.forEach { category ->
                val deferred = categories.getOrPut(category.id) { CompletableDeferred() }
                deferred.complete(category)
            }
        }
        return fetchedCategories
    }
}