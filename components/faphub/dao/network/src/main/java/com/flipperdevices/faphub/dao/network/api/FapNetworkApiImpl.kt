package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_APP_LOGO_URL
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_CATEGORY_LOGO_URL
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_CATEGORY_NAME
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_DELAY
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_DESCRIPTION
import com.flipperdevices.faphub.dao.network.model.MockConstants.MOCK_NAME
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class, FapNetworkApi::class)
class FapNetworkApiImpl @Inject constructor() : FapNetworkApi, LogTagProvider {
    override val TAG = "FapNetworkApi"
    override suspend fun getFeaturedItem(): FapItem = withContext(Dispatchers.IO) {
        debug { "Request featured item" }

        delay(MOCK_DELAY)

        val item = FapItem(
            picUrl = MOCK_APP_LOGO_URL,
            description = MOCK_DESCRIPTION,
            name = MOCK_NAME,
            category = FapCategory(
                name = MOCK_CATEGORY_NAME,
                picUrl = MOCK_CATEGORY_LOGO_URL
            )
        )
        debug { "Provider feature item: $item" }

        return@withContext item
    }
}