package com.flipperdevices.newfilemanager.impl.viewmodels

import android.content.Context
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.deeplink.model.cleanUp
import com.flipperdevices.deeplink.model.openStream
import com.flipperdevices.newfilemanager.impl.model.DownloadProgress
import com.flipperdevices.newfilemanager.impl.model.ShareState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import okio.source
import okio.use
import kotlin.io.path.Path

class ReceiveViewModel @AssistedInject constructor(
    @Assisted private val path: String,
    @Assisted private val deeplinkContent: DeeplinkContent,
    context: Context,
    private val featureProvider: FFeatureProvider,
) : CommonShareViewModel(
    featureProvider = featureProvider,
    fileName = deeplinkContent.filename() ?: "Unknown",
    defaultProgress = deeplinkContent.length()?.let {
        DownloadProgress.Fixed(totalSize = it)
    } ?: DownloadProgress.Infinite()
),
    LogTagProvider {
    override val TAG = "ReceiveViewModel"

    private val contentResolver = context.contentResolver

    override suspend fun start(
        storageFeatureApi: FStorageFeatureApi
    ): Unit = withContext(FlipperDispatchers.workStealingDispatcher) {
        info { "Upload file $deeplinkContent in $path start" }
        val fileStream = deeplinkContent.openStream(contentResolver) ?: return@withContext
        runCatching {
            fileStream.use { outputStream ->
                storageFeatureApi
                    .uploadApi()
                    .sink(Path(path).resolve(fileName).toString())
                    .use { sink ->
                        outputStream.source().copyWithProgress(
                            sink = sink,
                            sourceLength = {
                                deeplinkContent.length()
                            },
                            progressListener = { current, max ->
                                shareStateFlow.emit(
                                    ShareState.Ready(
                                        name = fileName,
                                        downloadProgress = DownloadProgress.Fixed(current, max)
                                    )
                                )
                            }
                        )
                    }
            }
        }.onSuccess {
            shareStateFlow.update {
                if (it is ShareState.Ready) {
                    it.copy(processCompleted = true)
                } else {
                    it
                }
            }
        }.onFailure { exception ->
            error(exception) { "Fail upload file" }
            shareStateFlow.emit(ShareState.Error)
        }

        deeplinkContent.cleanUp(contentResolver)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            deeplinkContent: DeeplinkContent,
            path: String
        ): ReceiveViewModel
    }
}
