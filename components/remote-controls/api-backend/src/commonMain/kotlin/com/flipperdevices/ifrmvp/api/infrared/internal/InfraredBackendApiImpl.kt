package com.flipperdevices.ifrmvp.api.infrared.internal

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.infrared.InfraredBackendApi
import com.flipperdevices.ifrmvp.api.infrared.model.InfraredHost
import com.flipperdevices.ifrmvp.backend.model.BrandsResponse
import com.flipperdevices.ifrmvp.backend.model.CategoriesResponse
import com.flipperdevices.ifrmvp.backend.model.IfrFileContentResponse
import com.flipperdevices.ifrmvp.backend.model.InfraredsResponse
import com.flipperdevices.ifrmvp.backend.model.PagesLayoutBackendModel
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

@ContributesBinding(AppGraph::class, InfraredBackendApi::class)
class InfraredBackendApiImpl(
    private val httpClient: HttpClient,
    private val host: InfraredHost = InfraredHost.DEV
) : InfraredBackendApi {
    @Inject
    constructor(httpClient: HttpClient) : this(httpClient, InfraredHost.DEV)

    override suspend fun getCategories(): CategoriesResponse {
        return httpClient.get {
            url("${host.url}/categories")
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getManufacturers(categoryId: Long): BrandsResponse {
        return httpClient.get {
            url("${host.url}/brands")
            parameter("category_id", categoryId)
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getSignal(request: SignalRequestModel): SignalResponseModel {
        return httpClient.post {
            url("${host.url}/signal")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getIfrFileContent(ifrFileId: Long): IfrFileContentResponse {
        return httpClient.get {
            url("${host.url}/key")
            parameter("ifr_file_id", ifrFileId)
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getUiFile(ifrFileId: Long): PagesLayoutBackendModel {
        return httpClient.get {
            url("${host.url}/ui")
            parameter("ifr_file_id", ifrFileId)
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getInfrareds(brandId: Long): InfraredsResponse {
        return httpClient.get {
            url("${host.url}/infrareds")
            parameter("brand_id", brandId)
            contentType(ContentType.Application.Json)
        }.body()
    }
}
