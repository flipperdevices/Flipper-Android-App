package com.flipperdevices.faphub.dao.network.retrofit.utils

import com.flipperdevices.core.log.TaggedTimber
import com.flipperdevices.core.log.verbose
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitDebugLoggingEnable {
    private val timber = TaggedTimber("DebugLog")

    fun enableDebugLogging(builder: Retrofit.Builder): Retrofit.Builder {
        val logging = HttpLoggingInterceptor {
            timber.verbose { it }
        }
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return builder.client(httpClient.build())
    }
}
