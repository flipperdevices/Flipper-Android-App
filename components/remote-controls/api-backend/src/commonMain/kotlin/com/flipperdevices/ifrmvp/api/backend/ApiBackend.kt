package com.flipperdevices.ifrmvp.api.backend

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.backend.model.BrandsResponse
import com.flipperdevices.ifrmvp.backend.model.CategoriesResponse
import com.flipperdevices.ifrmvp.backend.model.IfrFileContentResponse
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import com.squareup.anvil.annotations.ContributesBinding

interface ApiBackend {
    suspend fun getCategories(): CategoriesResponse
    suspend fun getManufacturers(categoryId: Long): BrandsResponse
    suspend fun getSignal(request: SignalRequestModel): SignalResponseModel
    suspend fun getIfrFileContent(ifrFileId: Long): IfrFileContentResponse
    suspend fun getUiFile(ifrFileId: Long): String
}
private const val HOST = "192.168.0.100:8080"

@ContributesBinding(AppGraph::class, ApiBackend::class)
class ApiBackendImpl @Inject constructor(
    private val httpClient: HttpClient
) : ApiBackend {
    override suspend fun getCategories(): CategoriesResponse {
        return httpClient.get {
            url(host = HOST, path = "categories")
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getManufacturers(categoryId: Long): BrandsResponse {
        return httpClient.get {
            url(host = HOST, path = "brands") {
                parameter("category_id", categoryId)
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getSignal(request: SignalRequestModel): SignalResponseModel {
        return httpClient.post {
            url(host = HOST, path = "signal")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getIfrFileContent(ifrFileId: Long): IfrFileContentResponse {
        return httpClient.get {
            url(host = HOST, path = "key")
            contentType(ContentType.Application.Json)
            parameter("ifr_file_id", ifrFileId)
        }.body()
    }

    override suspend fun getUiFile(ifrFileId: Long): String {
        return httpClient.get {
            url(host = HOST, path = "ui")
            contentType(ContentType.Application.Json)
            parameter("ifr_file_id", ifrFileId)
        }.bodyAsText()
    }
}
