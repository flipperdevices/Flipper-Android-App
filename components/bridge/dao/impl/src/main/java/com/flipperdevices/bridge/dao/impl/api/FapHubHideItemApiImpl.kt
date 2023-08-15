package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.FapHubHideItemApi
import com.flipperdevices.bridge.dao.api.model.FapHubHideItem
import com.flipperdevices.bridge.dao.impl.model.HideFapHubApp
import com.flipperdevices.bridge.dao.impl.repository.HideFapHubAppDao
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesBinding(AppGraph::class, FapHubHideItemApi::class)
class FapHubHideItemApiImpl @Inject constructor(
    private val fapHubAppDao: HideFapHubAppDao
) : FapHubHideItemApi {
    override fun getHiddenItems(): Flow<List<FapHubHideItem>> {
        return fapHubAppDao.fetchAllHideFapHub()
            .map { list -> list.map { FapHubHideItem(it.applicationUid) } }
    }

    override suspend fun isHidden(applicationUid: String): Boolean {
        return fapHubAppDao.getOneItem(applicationUid) != null
    }

    override suspend fun hideItem(applicationUid: String) {
        fapHubAppDao.insert(HideFapHubApp(applicationUid = applicationUid))
    }

    override suspend fun unHideItem(applicationUid: String) {
        val hiddenItem = fapHubAppDao.getOneItem(applicationUid) ?: return
        fapHubAppDao.delete(hiddenItem)
    }
}