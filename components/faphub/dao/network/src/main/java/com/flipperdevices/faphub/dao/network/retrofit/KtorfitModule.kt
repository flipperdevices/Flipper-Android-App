package com.flipperdevices.faphub.dao.network.retrofit

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitApplicationApi
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitBundleApi
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitCategoryApi
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitVersionApi
import com.flipperdevices.faphub.dao.network.retrofit.utils.FapHubNetworkCategoryApi
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private const val FAP_URL_DEV = "https://catalog.flipp.dev/api/v0/"

private const val FAP_URL = "https://catalog.flipperzero.one/api/v0/"

@Module
@ContributesTo(AppGraph::class)
class KtorfitModule {
    @Provides
    @Reusable
    fun provideRetrofit(
        httpClient: HttpClient,
        settings: DataStore<Settings>
    ): Ktorfit {
        val useDevCatalog = runBlocking { settings.data.first().useDevCatalog }
        val url = if (useDevCatalog) {
            FAP_URL_DEV
        } else {
            FAP_URL
        }
        return Ktorfit.Builder()
            .baseUrl(url)
            .httpClient(httpClient)
            .build()
    }

    @Provides
    @Reusable
    fun provideApplicationApi(ktorfit: Ktorfit): KtorfitApplicationApi {
        return ktorfit.create()
    }

    @Provides
    @Reusable
    fun provideNetworkCategoryApi(ktorfit: Ktorfit): FapHubNetworkCategoryApi {
        val categoryApi = ktorfit.create<KtorfitCategoryApi>()
        return FapHubNetworkCategoryApi(categoryApi)
    }

    @Provides
    @Reusable
    fun provideBundleApi(ktorfit: Ktorfit): KtorfitBundleApi {
        return ktorfit.create()
    }

    @Provides
    @Reusable
    fun provideVersionApi(ktorfit: Ktorfit): KtorfitVersionApi {
        return ktorfit.create()
    }
}
