package com.flipperdevices.updater.impl.tasks

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.ProtobufConstants
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.md5
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.md5sumRequest
import com.flipperdevices.protobuf.storage.writeRequest
import com.flipperdevices.protobuf.system.System
import com.flipperdevices.protobuf.system.rebootRequest
import com.flipperdevices.protobuf.system.updateRequest
import com.flipperdevices.updater.impl.model.IntFlashFullException
import com.flipperdevices.updater.model.UpdatingState
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface UploadToFlipperHelper {
    suspend fun uploadToFlipper(
        scope: CoroutineScope,
        flipperPath: String,
        updaterFolder: File,
        requestApi: FlipperRequestApi,
        stateListener: suspend (UpdatingState) -> Unit
    )
}

@ContributesBinding(AppGraph::class, UploadToFlipperHelper::class)
class UploadToFlipperHelperImpl @Inject constructor() : UploadToFlipperHelper, LogTagProvider {
    override val TAG = "UploadToFlipperHelper"

    override suspend fun uploadToFlipper(
        scope: CoroutineScope,
        flipperPath: String,
        updaterFolder: File,
        requestApi: FlipperRequestApi,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        upload(
            requestApi,
            updaterFolder,
            flipperPath
        ) { sended, totalBytes ->
            scope.launch(Dispatchers.Default) {
                stateListener(
                    UpdatingState.UploadOnFlipper(
                        sended.toFloat() / totalBytes.toFloat()
                    )
                )
            }
        }

        val response = requestApi.request(
            main {
                systemUpdateRequest = updateRequest {
                    updateManifest = "$flipperPath/update.fuf"
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).first()
        if (response.commandStatus != Flipper.CommandStatus.OK) {
            error("Failed send update request")
        }

        requestApi.requestWithoutAnswer(
            main {
                systemRebootRequest = rebootRequest {
                    mode = System.RebootRequest.RebootMode.UPDATE
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        )
    }

    private suspend fun upload(
        requestApi: FlipperRequestApi,
        folder: File,
        pathOnFlipper: String,
        onProgressUpdate: (Long, Long) -> Unit
    ) {
        val fileList = folder.walk().filterNot { it.isDirectory }.toList().filterNot {
            if (fileAlreadyUploaded(requestApi, it, File(pathOnFlipper, it.name).path)) {
                info { "Skip $it because file already uploaded" }
                return@filterNot true
            } else return@filterNot false
        }
        var totalBytesSend: Long = 0
        var totalSize: Long = 0
        fileList.forEach {
            totalSize += it.length()
        }

        info { "Start upload $fileList" }

        fileList.forEach { singleFile ->
            val flipperFilePath = File(pathOnFlipper, singleFile.name).path
            singleFile.inputStream().use { inputStream ->
                val requestFlow = streamToCommandFlow(
                    inputStream,
                    singleFile.length()
                ) { chunkData ->
                    storageWriteRequest = writeRequest {
                        path = flipperFilePath
                        file = file { data = chunkData }
                    }
                }.map {
                    FlipperRequest(
                        data = it,
                        onSendCallback = {
                            totalBytesSend += ProtobufConstants.MAX_FILE_DATA
                            onProgressUpdate(totalBytesSend, totalSize)
                        }
                    )
                }
                val response = requestApi.request(requestFlow)
                when (response.commandStatus) {
                    Flipper.CommandStatus.ERROR_INVALID_PARAMETERS -> throw IntFlashFullException()
                    Flipper.CommandStatus.OK -> {}
                    else -> error("Failed with $response")
                }
            }
        }
    }

    private suspend fun fileAlreadyUploaded(
        requestApi: FlipperRequestApi,
        file: File,
        pathOnFlipper: String
    ): Boolean {
        val fileMd5 = file.inputStream().use {
            it.md5()
        }

        val response = requestApi.request(
            main {
                storageMd5SumRequest = md5sumRequest {
                    path = pathOnFlipper
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).first()
        if (response.hasStorageMd5SumResponse()) {
            return response.storageMd5SumResponse.md5Sum == fileMd5
        }
        return false
    }
}
