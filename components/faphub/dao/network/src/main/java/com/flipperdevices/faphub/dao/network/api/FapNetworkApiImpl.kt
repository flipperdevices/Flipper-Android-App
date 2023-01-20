package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_CATEGORY_LOGO_URL
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_CATEGORY_NAME
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_DELAY
import com.flipperdevices.faphub.dao.network.model.MockConstants.getMockItem
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapNetworkApi::class)
class FapNetworkApiImpl @Inject constructor() : FapNetworkApi, LogTagProvider {
    override val TAG = "FapNetworkApi"
    override suspend fun getFeaturedItem(): FapItem = withContext(Dispatchers.IO) {
        debug { "Request featured item" }

        delay(MOCK_DELAY)

        val item = getMockItem()
        debug { "Provider feature item: $item" }

        return@withContext item
    }

    override suspend fun getAllItem(
        category: FapCategory?,
        sortType: SortType
    ): List<FapItem> = withContext(Dispatchers.IO) {
        delay(MOCK_DELAY)
        return@withContext MutableList(size = 10) { getMockItem() }
    }

    override suspend fun search(query: String) = getAllItem(sortType = SortType.UPDATED)

    override suspend fun getCategories(): List<FapCategory> = withContext(Dispatchers.IO) {
        debug { "Request categories" }
        delay(MOCK_DELAY)
        return@withContext MutableList(size = 10) {
            FapCategory(
                name = MOCK_CATEGORY_NAME,
                picUrl = MOCK_CATEGORY_LOGO_URL
            )
        }
    }

    override suspend fun getFapItemById(id: String): FapItem = withContext(Dispatchers.IO) {
        debug { "Request fap item by id $id" }
        delay(MOCK_DELAY)
        return@withContext getMockItem().copy(id = id)
    }
}
