package com.flipperdevices.faphub.dao.network.network.api

import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.faphub.dao.network.network.model.FapNetworkHostEnum
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.prepareGet
import io.ktor.client.request.url
import io.ktor.client.statement.HttpStatement
import javax.inject.Inject
import javax.inject.Provider

class FapNetworkBundleApi @Inject constructor(
    private val httpClient: HttpClient,
    fapHostProvider: Provider<FapNetworkHostEnum>,
) {
    private val fapHost by fapHostProvider

    suspend fun downloadBundle(
        versionUid: String,
        target: String,
        sdkApi: String
    ): HttpStatement {
        return httpClient.prepareGet {
            url("${fapHost.baseUrl}/v0/0/application/version/$versionUid/build/compatible")
            parameter("target", target)
            parameter("api", sdkApi)
        }
    }
}
