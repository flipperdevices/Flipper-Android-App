package com.flipperdevices.faphub.installation.queue.impl.executor.actions

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.util.Base64
import javax.inject.Inject

class FapIconDownloader @Inject constructor(
    private val client: HttpClient
) : LogTagProvider {
    override val TAG = "FapIconDownloader"

    suspend fun downloadToBase64(picUrl: String): Result<String> = runCatching {
        info { "Download $picUrl" }
        val imageBytes = client.get(picUrl).body<ByteArray>()
        return@runCatching Base64.getEncoder().encodeToString(imageBytes)
    }
}
