package com.flipperdevices.share.cryptostorage.helper

import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import java.io.FileNotFoundException
import java.net.UnknownServiceException
import javax.inject.Inject

private const val STORAGE_URL = "https://transfer.flpr.app/"
private const val STORAGE_NAME = "hakuna-matata"

interface StorageHelperApi {
    suspend fun upload(data: ByteArray): String
    suspend fun download(id: String): ByteArray
}

@ContributesBinding(AppGraph::class)
class StorageHelper @Inject constructor(
    private val client: HttpClient
) : StorageHelperApi {
    @OptIn(InternalAPI::class)
    override suspend fun upload(data: ByteArray): String {
        val response = client.put(
            urlString = "$STORAGE_URL$STORAGE_NAME"
        ) {
            body = data
        }

        when (response.status) {
            HttpStatusCode.OK -> {}
            else -> throw UnknownServiceException("")
        }

        return response.body()
    }

    override suspend fun download(id: String): ByteArray {
        val response = client.get(urlString = "$STORAGE_URL$id/$STORAGE_NAME")

        when (response.status) {
            HttpStatusCode.OK -> {}
            HttpStatusCode.NotFound -> throw FileNotFoundException()
            else -> throw UnknownServiceException("")
        }

        return response.body()
    }
}
