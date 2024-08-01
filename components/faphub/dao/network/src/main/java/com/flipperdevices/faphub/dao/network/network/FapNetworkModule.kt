package com.flipperdevices.faphub.dao.network.network

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.faphub.dao.network.network.model.FapNetworkHostEnum
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Module
@ContributesTo(AppGraph::class)
class FapNetworkModule {
    @Provides
    @Reusable
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
