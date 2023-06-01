package com.flipperdevices.faphub.dao.network.retrofit

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.BuildConfig
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitApplicationApi
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitBundleApi
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitCategoryApi
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitVersionApi
import com.flipperdevices.faphub.dao.network.retrofit.utils.FapHubNetworkCategoryApi
import com.flipperdevices.faphub.dao.network.retrofit.utils.RetrofitDebugLoggingEnable
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

private const val FAP_URL = "https://catalog.flipp.dev/api/v0/"

@Module
@ContributesTo(AppGraph::class)
class RetrofitModule {
    private val json by lazy {
        Json {
            ignoreUnknownKeys = !BuildConfig.DEBUG
        }
    }

    @Provides
    @Reusable
    @Suppress("")
    fun provideRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        var builder = Retrofit.Builder()
            .baseUrl(FAP_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
        if (BuildConfig.INTERNAL) {
            builder = RetrofitDebugLoggingEnable.enableDebugLogging(builder)
        }
        return builder.build()
    }

    @Provides
    @Reusable
    fun provideApplicationApi(retrofit: Retrofit): RetrofitApplicationApi {
        return retrofit.create(RetrofitApplicationApi::class.java)
    }

    @Provides
    @Reusable
    fun provideNetworkCategoryApi(retrofit: Retrofit): FapHubNetworkCategoryApi {
        val categoryApi = retrofit.create(RetrofitCategoryApi::class.java)
        return FapHubNetworkCategoryApi(categoryApi)
    }

    @Provides
    @Reusable
    fun provideBundleApi(retrofit: Retrofit): RetrofitBundleApi {
        return retrofit.create(RetrofitBundleApi::class.java)
    }

    @Provides
    @Reusable
    fun providerVersionApi(retrofit: Retrofit): RetrofitVersionApi {
        return retrofit.create(RetrofitVersionApi::class.java)
    }
}
