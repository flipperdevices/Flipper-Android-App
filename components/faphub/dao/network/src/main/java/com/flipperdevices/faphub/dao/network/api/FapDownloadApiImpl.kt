package com.flipperdevices.faphub.dao.network.api

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.copyTo
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.dao.network.retrofit.api.RetrofitBundleApi
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.squareup.anvil.annotations.ContributesBinding
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapDownloadApi::class)
class FapDownloadApiImpl @Inject constructor(
    private val bundleApi: RetrofitBundleApi,
    private val flipperTargetApi: FlipperTargetProviderApi,
    private val context: Context
) : FapDownloadApi, LogTagProvider {
    override val TAG = "FapDownloadApi"

    override suspend fun downloadBundle(
        versionId: String,
        listener: ProgressListener?
    ): File {
        info { "Start download bundle for $versionId" }
        val target = flipperTargetApi.getFlipperTargetSync().getOrThrow()

        val file = FlipperStorageProvider.getTemporaryFile(context)

        bundleApi.downloadBundle(versionId, target.target, target.sdk.toString())
            .saveToFile(file, listener?.let { ProgressWrapperTracker(it) })
        info { "Complete download for $versionId" }

        return file
    }
}

private suspend fun ResponseBody.saveToFile(
    file: File,
    listener: ProgressWrapperTracker?
) = byteStream().use { streamFromNetwork ->
    file.outputStream().use { fileStream ->
        val totalBytes = contentLength()
        streamFromNetwork.copyTo(fileStream, onProcessed = { bytesSentTotal ->
            if (totalBytes > 0) {
                listener?.report(bytesSentTotal, totalBytes)
            }
        })
    }
}
