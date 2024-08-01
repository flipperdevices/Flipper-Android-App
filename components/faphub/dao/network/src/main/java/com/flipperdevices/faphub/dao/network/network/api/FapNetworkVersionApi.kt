package com.flipperdevices.faphub.dao.network.network.api

import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.faphub.dao.network.network.model.FapNetworkHostEnum
import com.flipperdevices.faphub.dao.network.network.model.KtorfitDetailedVersion
import com.flipperdevices.faphub.dao.network.network.model.requests.KtorfitDetailedVersionRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import javax.inject.Inject
import javax.inject.Provider

class FapNetworkVersionApi @Inject constructor(
    private val httpClient: HttpClient,
    fapHostProvider: Provider<FapNetworkHostEnum>,
) {
    private val fapHost by fapHostProvider

    suspend fun getVersions(
        versionRequest: KtorfitDetailedVersionRequest
    ): List<KtorfitDetailedVersion> {
        return httpClient.post {
            url("${fapHost.baseUrl}/v0/1/application/versions")
            setBody(versionRequest)
        }.body()
    }
}
