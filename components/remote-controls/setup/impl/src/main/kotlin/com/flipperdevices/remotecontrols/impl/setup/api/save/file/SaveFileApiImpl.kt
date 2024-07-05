package com.flipperdevices.remotecontrols.impl.setup.api.save.file

import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SaveFileApi::class)
class SaveFileApiImpl @Inject constructor(
    private val context: Context
) : LogTagProvider, SaveFileApi {
    override val TAG: String = "SaveFileApi"
    override fun save(
        requestApi: FlipperRequestApi,
        deeplinkContent: DeeplinkContent,
        ffPath: FlipperFilePath
    ): Flow<SaveFileApi.Status> = channelFlow {
        val totalSize = deeplinkContent.length() ?: 0
        var progressInternal = 0L
        val contentResolver = context.contentResolver
        deeplinkContent.openStream(contentResolver).use { fileStream ->
            val stream = fileStream ?: return@use
            val commandFlow = streamToCommandFlow(
                stream,
                deeplinkContent.length(),
                requestWrapper = { chunkData ->
                    storageWriteRequest = writeRequest {
                        path = ffPath.getPathOnFlipper()
                        file = file { data = chunkData }
                    }
                }
            )
            val requestFlow = commandFlow.map { message ->
                FlipperRequest(
                    data = message,
                    priority = FlipperRequestPriority.FOREGROUND,
                    onSendCallback = {
                        val size = message.storageWriteRequest.file.data.size().toLong()
                        progressInternal += size
                        val status = SaveFileApi.Status.Saving(
                            progressInternal,
                            totalSize
                        )
                        send(status)
                    }
                )
            }
            val response = requestApi.request(
                commandFlow = requestFlow,
                onCancel = { id ->
                    requestApi.request(
                        main {
                            commandId = id
                            hasNext = false
                            storageWriteRequest = writeRequest {
                                path = ffPath.getPathOnFlipper()
                            }
                        }.wrapToRequest(FlipperRequestPriority.RIGHT_NOW)
                    ).collect()
                    send(SaveFileApi.Status.Finished)
                }
            )
            info { "File send with response $response" }
        }
    }
}
