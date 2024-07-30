package com.flipperdevices.faphub.dao.network.network.api

import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.faphub.dao.network.network.model.FapNetworkHostEnum
import com.flipperdevices.faphub.dao.network.network.model.KtorfitApplicationShort
import com.flipperdevices.faphub.dao.network.network.model.KtorfitReport
import com.flipperdevices.faphub.dao.network.network.model.detailed.KtorfitApplicationDetailed
import com.flipperdevices.faphub.dao.network.network.model.requests.KtorfitApplicationApiRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import javax.inject.Inject
import javax.inject.Provider

class FapNetworkApplicationApi @Inject constructor(
    private val httpClient: HttpClient,
    fapHostProvider: Provider<FapNetworkHostEnum>,
) {
    private val fapHost by fapHostProvider

    suspend fun getAll(
        applicationApiRequest: KtorfitApplicationApiRequest
    ): List<KtorfitApplicationShort> {
        return httpClient.post {
            url("${fapHost.baseUrl}/v0/1/application")
            setBody(applicationApiRequest)
        }.body()
    }

    suspend fun getFeaturedApps(
        limit: Int = 50,
        offset: Int = 0
    ): List<KtorfitApplicationShort> {
        return httpClient.get {
            url("${fapHost.baseUrl}/v0/0/application/featured")
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    suspend fun get(
        id: String,
        target: String? = null,
        sdkApiVersion: String? = null,
    ): KtorfitApplicationDetailed {
        return httpClient.get {
            url("${fapHost.baseUrl}/v0/0/application/$id")
            parameter("target", target)
            parameter("api", sdkApiVersion)
        }.body()
    }

    suspend fun report(applicationUid: String, report: KtorfitReport) {
        httpClient.post {
            url("${fapHost.baseUrl}/v0/0/application/$applicationUid/issue")
            setBody(report)
        }
    }
}
