package com.flipperdevices.share.receive.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.ProtobufConstants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.ui.AndroidLifecycleViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.flipperdevices.share.common.model.DownloadProgress
import com.flipperdevices.share.common.model.ShareState
import com.flipperdevices.share.receive.di.ShareReceiveComponent
import com.flipperdevices.share.receive.util.FAILED_SIZE
import com.flipperdevices.share.receive.util.lengthAsync
import com.google.protobuf.ByteString
import java.io.File
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val EOF_CODE = -1

class ReceiveViewModel(
    private val deeplinkContent: DeeplinkContent,
    private val flipperPath: String,
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "ReceiveViewModel"

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    private val uploadStarted = AtomicBoolean(false)
    private val contentResolver = application.contentResolver
    private val receiveStateFlow = MutableStateFlow(
        ShareState(
            DownloadProgress.Infinite(0L)
        )
    )

    init {
        ComponentHolder.component<ShareReceiveComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getReceiveState(): StateFlow<ShareState> = receiveStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch {
            startUpload(serviceApi)
        }
    }

    fun cancelUpload() {
        receiveStateFlow.update {
            it.copy(dialogShown = false)
        }
    }

    private suspend fun startUpload(serviceApi: FlipperServiceApi) = withContext(Dispatchers.IO) {
        if (!uploadStarted.compareAndSet(false, true)) {
            info { "Upload file $deeplinkContent already started" }
            return@withContext
        }
        val fileSize = calculateFileLengthAsync()
        info { "Upload file $deeplinkContent start" }
        val exception = runCatching {
            deeplinkContent.openStream().use { fileStream ->
                val stream = fileStream ?: return@use
                val requestFlow = getUploadRequestFlow(stream, fileSize).map {
                    it.wrapToRequest(FlipperRequestPriority.FOREGROUND)
                }
                val response = serviceApi.requestApi.request(requestFlow)
                info { "File send with response $response" }
            }
        }.exceptionOrNull()
        cleanUp()
        receiveStateFlow.update {
            it.copy(
                dialogShown = false,
                processCompleted = true
            )
        }
        if (exception != null) {
            error(exception) { "Can't upload $deeplinkContent" }
        }
    }

    private suspend fun getUploadRequestFlow(
        fileStream: InputStream,
        fileSize: Long?
    ) = channelFlow {
        val filePath = File(flipperPath, deeplinkContent.filename()!!).absolutePath

        val bufferArray = ByteArray(ProtobufConstants.MAX_FILE_DATA)
        var alreadyRead = 0L
        var isAllRead = false
        var readSize = fileStream.readOrThrow(bufferArray)

        while (readSize != EOF_CODE && !isAllRead) {
            alreadyRead += readSize
            isAllRead = if (fileSize != null) alreadyRead >= fileSize else false
            send(
                main {
                    hasNext = isAllRead.not()
                    storageWriteRequest = writeRequest {
                        path = filePath
                        file = file {
                            data = ByteString.copyFrom(bufferArray.copyOf(readSize))
                        }
                    }
                }
            )
            readSize = fileStream.readOrThrow(bufferArray)
        }
        if (!isAllRead) {
            warn { "Unexpected end of stream. Expect $fileSize bytes, actual $alreadyRead bytes" }
            send(
                main {
                    hasNext = false
                    storageWriteRequest = writeRequest {
                        path = filePath
                        file = file {
                            data = ByteString.EMPTY
                        }
                    }
                }
            )
        }
        close()
    }

    private suspend fun calculateFileLengthAsync(): Long? {
        val fileLength = when (deeplinkContent) {
            is DeeplinkContent.ExternalUri -> {
                deeplinkContent.uri.lengthAsync(contentResolver)
            }
            is DeeplinkContent.InternalStorageFile -> {
                deeplinkContent.file.length()
            }
        }
        info { "Calculate size for $deeplinkContent is $fileLength" }
        if (fileLength != FAILED_SIZE) {
            receiveStateFlow.update {
                it.copy(
                    downloadProgress = DownloadProgress.Fixed(
                        it.downloadProgress.progress,
                        fileLength
                    )
                )
            }
            return fileLength
        }
        return null
    }

    private fun DeeplinkContent.openStream(): InputStream? {
        return when (this) {
            is DeeplinkContent.ExternalUri -> {
                contentResolver.openInputStream(uri)
            }
            is DeeplinkContent.InternalStorageFile -> {
                file.inputStream()
            }
        }
    }

    private fun InputStream.readOrThrow(buffer: ByteArray): Int {
        return runCatching {
            read(buffer)
        }.getOrThrow()
    }

    private fun cleanUp() {
        when (deeplinkContent) {
            is DeeplinkContent.ExternalUri -> {
                contentResolver.releasePersistableUriPermission(
                    deeplinkContent.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            is DeeplinkContent.InternalStorageFile -> {
                deeplinkContent.file.delete()
            }
        }
    }
}
