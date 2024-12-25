package com.flipperdevices.updater.impl.tasks

import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcInvalidParametersException
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.update.api.FUpdateFeatureApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.md5
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.protobuf.system.RebootRequest
import com.flipperdevices.protobuf.system.UpdateResponse
import com.flipperdevices.updater.impl.model.IntFlashFullException
import com.flipperdevices.updater.model.UpdatingState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import java.io.File
import javax.inject.Inject

interface UploadToFlipperHelper {
    suspend fun uploadToFlipper(
        flipperPath: String,
        updaterFolder: File,
        fUpdateFeatureApi: FUpdateFeatureApi,
        fListingStorageApi: FListingStorageApi,
        fFileUploadApi: FFileUploadApi,
        stateListener: suspend (UpdatingState) -> Unit
    )
}

@ContributesBinding(AppGraph::class, UploadToFlipperHelper::class)
class UploadToFlipperHelperImpl @Inject constructor() : UploadToFlipperHelper, LogTagProvider {
    override val TAG = "UploadToFlipperHelper"

    override suspend fun uploadToFlipper(
        flipperPath: String,
        updaterFolder: File,
        fUpdateFeatureApi: FUpdateFeatureApi,
        fListingStorageApi: FListingStorageApi,
        fFileUploadApi: FFileUploadApi,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        upload(
            folder = updaterFolder,
            pathOnFlipper = flipperPath,
            fListingStorageApi = fListingStorageApi,
            fFileUploadApi = fFileUploadApi,
            progressListener = { percent ->
                withContext(FlipperDispatchers.workStealingDispatcher) {
                    stateListener(
                        UpdatingState.UploadOnFlipper(
                            percent = percent
                        )
                    )
                }
            },
        )
        try {
            fUpdateFeatureApi.systemUpdate("$flipperPath/update.fuf")
        } catch (e: FRpcInvalidParametersException) {
            val code = e.response.system_update_response?.code
            if (code == UpdateResponse.UpdateResultCode.IntFull) {
                throw IntFlashFullException()
            }
        } catch (e: FRpcException) {
            error("Failed send update request with status ${e.response.command_status}")
        } catch (e: Exception) {
            error("Failed send update request with status unknown exception: ${e.message}")
        }

        fUpdateFeatureApi.reboot(RebootRequest.RebootMode.UPDATE)
            .onFailure { error(it) { "#uploadToFlipper could not reboot device" } }
    }

    private suspend fun upload(
        folder: File,
        pathOnFlipper: String,
        progressListener: ProgressListener,
        fListingStorageApi: FListingStorageApi,
        fFileUploadApi: FFileUploadApi,
    ) {
        val fileList = folder.walk().filterNot { it.isDirectory }.toList().filterNot { file ->
            val isAlreadyUploaded = fileAlreadyUploaded(
                file = file,
                pathOnFlipper = pathOnFlipper.toPath().resolve(file.name),
                fListingStorageApi = fListingStorageApi
            )
            if (isAlreadyUploaded) {
                info { "Skip $file because file already uploaded" }
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
            fFileUploadApi.upload(
                pathOnFlipper = flipperFilePath,
                fileOnAndroid = singleFile.toOkioPath(),
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
        file: File,
        pathOnFlipper: Path,
        fListingStorageApi: FListingStorageApi
    ): Boolean {
        val fileMd5 = file.inputStream()
            .use { fis -> fis.md5() }
        val md5Response = fListingStorageApi.lsWithMd5(pathOnFlipper.toString())
            .onFailure { error(it) { "#fileAlreadyUploaded Could not get lsWithMd5" } }
            .getOrNull()
            ?.firstOrNull()
        return md5Response?.md5 == fileMd5
    }
}
