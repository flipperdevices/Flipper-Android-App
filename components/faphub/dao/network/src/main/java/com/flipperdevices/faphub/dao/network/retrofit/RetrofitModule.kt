package com.flipperdevices.faphub.dao.network.retrofit

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.BuildConfig
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitApplicationApi
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitCategoryApi
import com.flipperdevices.faphub.dao.network.retrofit.utils.FapHubNetworkCategoryApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

private const val FAP_URL = "https://catalog.flipp.dev/api/v0/"

@Module
@ContributesTo(AppGraph::class)
class RetrofitModule {
    @Provides
    @Reusable
    fun provideRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        var builder = Retrofit.Builder()
            .baseUrl(FAP_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            builder = builder.client(httpClient.build())
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
}
