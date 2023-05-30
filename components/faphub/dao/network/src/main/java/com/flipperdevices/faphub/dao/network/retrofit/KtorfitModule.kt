package com.flipperdevices.faphub.dao.network.retrofit

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitApplicationApi
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitBundleApi
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitCategoryApi
import com.flipperdevices.faphub.dao.network.retrofit.utils.FapHubNetworkCategoryApi
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

private const val FAP_URL = "https://catalog.flipp.dev/api/v0/"

@Module
@ContributesTo(AppGraph::class)
class KtorfitModule {
    @Provides
    @Reusable
    fun provideRetrofit(httpClient: HttpClient): Ktorfit {
        return Ktorfit.Builder()
            .baseUrl(FAP_URL)
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
}
