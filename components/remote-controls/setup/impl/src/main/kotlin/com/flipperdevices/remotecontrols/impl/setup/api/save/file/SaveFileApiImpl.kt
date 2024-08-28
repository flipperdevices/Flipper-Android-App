package com.flipperdevices.remotecontrols.impl.setup.api.save.file

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.io.ByteArrayInputStream
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SaveFileApi::class)
class SaveFileApiImpl @Inject constructor() : LogTagProvider, SaveFileApi {
    override val TAG: String = "SaveFileApi"
    override fun save(
        requestApi: FlipperRequestApi,
        textContent: String,
        absolutePath: String
    ): Flow<SaveFileApi.Status> = channelFlow {
        val byteArray = textContent.toByteArray()
        val totalSize = byteArray.size.toLong()
        var progressInternal = 0L
        info { "#save Saving content: size: $totalSize $textContent" }
        ByteArrayInputStream(byteArray).use { stream ->
            val commandFlow = streamToCommandFlow(
                stream,
                totalSize,
                requestWrapper = { chunkData ->
                    storageWriteRequest = writeRequest {
                        path = absolutePath
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
                            uploaded = progressInternal,
                            size = totalSize,
                            lastWriteSize = size
                        )
                        info { "#onSendCallback $status" }
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
                                path = absolutePath
                            }
                        }.wrapToRequest(FlipperRequestPriority.RIGHT_NOW)
                    ).collect()
                    send(SaveFileApi.Status.Finished)
                    info { "#onCancel" }
                }
            )
            info { "File send with response $response" }
        }
    }
}
