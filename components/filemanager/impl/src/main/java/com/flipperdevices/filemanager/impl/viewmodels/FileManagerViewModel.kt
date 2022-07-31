package com.flipperdevices.filemanager.impl.viewmodels

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.filemanager.impl.api.PATH_KEY
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.filemanager.impl.model.FileManagerState
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.Storage
import com.flipperdevices.protobuf.storage.listRequest
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class FileManagerViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    @TangleParam(PATH_KEY)
    private val directory: String
) : LifecycleViewModel(), FlipperBleServiceConsumer, LogTagProvider {
    override val TAG = "FileManagerViewModel"

    private var fileManagerStateFlow =
        MutableStateFlow(FileManagerState(currentPath = directory))

    init {
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getFileManagerState(): StateFlow<FileManagerState> = fileManagerStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch {
            serviceApi.requestApi.request(
                main {
                    storageListRequest = listRequest {
                        path = directory
                    }
                }.wrapToRequest()
            ).map {
                info { "FileManagerFragment#$directory" }
                it.storageListResponse.fileList.map { file ->
                    FileItem(
                        fileName = file.name,
                        isDirectory = file.type == Storage.File.FileType.DIR,
                        path = File(directory, file.name).absolutePath,
                        size = file.size.toLong()
                    )
                }
            }.collect { fileList ->
                fileManagerStateFlow.update { oldState ->
                    val newSet = oldState.filesInDirectory.plus(fileList)
                    oldState.copy(filesInDirectory = newSet)
                }
            }
            fileManagerStateFlow.update {
                it.copy(inProgress = false)
            }
        }
    }
}
