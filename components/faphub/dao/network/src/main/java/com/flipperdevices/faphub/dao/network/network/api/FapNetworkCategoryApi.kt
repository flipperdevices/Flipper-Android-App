package com.flipperdevices.faphub.dao.network.network.api

import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.faphub.dao.network.network.model.FapNetworkHostEnum
import com.flipperdevices.faphub.dao.network.network.model.KtorfitCategory
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import javax.inject.Provider

class FapNetworkCategoryApi(
    private val httpClient: HttpClient,
    fapHostProvider: Provider<FapNetworkHostEnum>,
) {
    private val fapHost by fapHostProvider

    suspend fun getAll(
        sdkApi: String?,
        target: String?
    ): List<KtorfitCategory> {
        return httpClient.get {
            url("${fapHost.baseUrl}/v0/0/category")
            parameter("api", sdkApi)
            parameter("target", target)
        }.body()
    }
}
