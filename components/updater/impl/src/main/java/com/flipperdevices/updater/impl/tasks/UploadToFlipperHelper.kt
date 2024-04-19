package com.flipperdevices.updater.impl.tasks

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.md5
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.md5sumRequest
import com.flipperdevices.protobuf.system.System
import com.flipperdevices.protobuf.system.rebootRequest
import com.flipperdevices.protobuf.system.updateRequest
import com.flipperdevices.updater.impl.model.IntFlashFullException
import com.flipperdevices.updater.model.UpdatingState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

interface UploadToFlipperHelper {
    suspend fun uploadToFlipper(
        flipperPath: String,
        updaterFolder: File,
        requestApi: FlipperRequestApi,
        stateListener: suspend (UpdatingState) -> Unit
    )
}

@ContributesBinding(AppGraph::class, UploadToFlipperHelper::class)
class UploadToFlipperHelperImpl @Inject constructor(
    private val flipperStorageApi: FlipperStorageApi
) : UploadToFlipperHelper, LogTagProvider {
    override val TAG = "UploadToFlipperHelper"

    override suspend fun uploadToFlipper(
        flipperPath: String,
        updaterFolder: File,
        requestApi: FlipperRequestApi,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        upload(
            requestApi,
            updaterFolder,
            flipperPath
        ) { percent ->
            withContext(FlipperDispatchers.workStealingDispatcher) {
                stateListener(
                    UpdatingState.UploadOnFlipper(
                        percent = percent
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
        when (response.commandStatus) {
            Flipper.CommandStatus.ERROR_INVALID_PARAMETERS -> {
                val code = response.systemUpdateResponse.code
                if (code == System.UpdateResponse.UpdateResultCode.IntFull) {
                    throw IntFlashFullException()
                }
            }

            Flipper.CommandStatus.OK -> {}
            else -> error("Failed send update request with status ${response.commandStatus}")
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
        progressListener: ProgressListener
    ) {
        val fileList = folder.walk().filterNot { it.isDirectory }.toList().filterNot {
            if (fileAlreadyUploaded(requestApi, it, File(pathOnFlipper, it.name).path)) {
                info { "Skip $it because file already uploaded" }
                return@filterNot true
            } else {
                return@filterNot false
            }
        }
        var sizeUploaded: Long = 0
        var totalSize: Long = 0
        fileList.forEach {
            totalSize += it.length()
        }

        info { "Start upload $fileList" }

        fileList.forEach { singleFile ->
            val flipperFilePath = File(pathOnFlipper, singleFile.name).path
            flipperStorageApi.upload(
                pathOnFlipper = flipperFilePath,
                fileOnAndroid = singleFile,
                progressListener = ProgressWrapperTracker(
                    progressListener = progressListener,
                    min = sizeUploaded.toFloat() / totalSize.toFloat(),
                    max = (sizeUploaded + singleFile.length()).toFloat() / totalSize.toFloat()
                )
            )
            sizeUploaded += singleFile.length()
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
