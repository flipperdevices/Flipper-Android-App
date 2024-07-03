package com.flipperdevices.ifrmvp.api.backend

import com.flipperdevices.ifrmvp.backend.model.BrandsResponse
import com.flipperdevices.ifrmvp.backend.model.CategoriesResponse
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ApiBackend {
    suspend fun getCategories(): CategoriesResponse
    suspend fun getManufacturers(categoryId: Long): BrandsResponse
    suspend fun getSignal(request: SignalRequestModel): SignalResponseModel
}

internal class ApiBackendImpl(
    private val httpClient: HttpClient,
    private val backendUrlHost: String
) : ApiBackend {
    override suspend fun getCategories(): CategoriesResponse {
        return httpClient.get {
            url(host = backendUrlHost, path = "categories")
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getManufacturers(categoryId: Long): BrandsResponse {
        return httpClient.get {
            url(host = backendUrlHost, path = "brands") {
                parameter("category_id", categoryId)
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getSignal(request: SignalRequestModel): SignalResponseModel {
        return httpClient.post {
            url(host = backendUrlHost, path = "signal")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
