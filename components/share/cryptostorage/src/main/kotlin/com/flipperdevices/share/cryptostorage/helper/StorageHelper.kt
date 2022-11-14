package com.flipperdevices.share.cryptostorage.helper

import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import java.net.UnknownServiceException
import javax.inject.Inject

private const val STORAGE_URL = "https://transfer.flpr.app/"

interface StorageHelperApi {
    suspend fun upload(data: ByteArray, name: String): String
    suspend fun download(id: String, name: String): ByteArray
}

@ContributesBinding(AppGraph::class)
class StorageHelper @Inject constructor(
    private val client: HttpClient
) : StorageHelperApi {
    @OptIn(InternalAPI::class)
    override suspend fun upload(data: ByteArray, name: String): String {
        val response = client.put(
            urlString = "$STORAGE_URL$name"
        ) {
            body = data
        }

        if (response.status != HttpStatusCode.OK) throw UnknownServiceException("")

        return response.body()
    }

    override suspend fun download(id: String, name: String): ByteArray {
        val response = client.get(urlString = "$STORAGE_URL$id/$name")
        if (response.status != HttpStatusCode.OK) throw UnknownServiceException("")

        return response.body()
    }
}
