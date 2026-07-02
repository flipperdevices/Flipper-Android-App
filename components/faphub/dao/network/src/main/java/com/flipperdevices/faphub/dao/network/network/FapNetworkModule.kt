package com.flipperdevices.faphub.dao.network.network

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.faphub.dao.network.network.model.FapNetworkHostEnum
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@ContributesTo(AppGraph::class)
interface FapNetworkModule {
    @Provides
    @SingleIn(AppGraph::class)
    fun provideFapNetworkHostEnum(
        settings: DataStore<Settings>
    ): FapNetworkHostEnum {
        val useDevCatalog = runBlocking { settings.data.first().use_dev_catalog }
        return when (useDevCatalog) {
            true -> FapNetworkHostEnum.DEV
            false -> FapNetworkHostEnum.PROD
        }
    }
}
