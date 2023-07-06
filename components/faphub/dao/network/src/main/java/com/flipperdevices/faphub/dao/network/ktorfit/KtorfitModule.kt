package com.flipperdevices.faphub.dao.network.ktorfit

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.dao.network.ktorfit.api.KtorfitApplicationApi
import com.flipperdevices.faphub.dao.network.ktorfit.api.KtorfitBundleApi
import com.flipperdevices.faphub.dao.network.ktorfit.api.KtorfitCategoryApi
import com.flipperdevices.faphub.dao.network.ktorfit.api.KtorfitVersionApi
import com.flipperdevices.faphub.dao.network.ktorfit.utils.FapHubNetworkCategoryApi
import com.flipperdevices.faphub.dao.network.ktorfit.utils.HostUrlBuilder
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking

@Module
@ContributesTo(AppGraph::class)
class KtorfitModule {
    @Provides
    @Reusable
    fun provideRetrofit(
        httpClient: HttpClient,
        hostUrlBuilder: HostUrlBuilder
    ): Ktorfit {
        val hostUrl = runBlocking { hostUrlBuilder.getHostUrl() }
        val url = "$hostUrl/api/v0/"
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
