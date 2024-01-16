package com.flipperdevices.filemanager.impl.viewmodels

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.impl.model.CreateFileManagerAction
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.filemanager.impl.model.FileManagerState
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.Storage
import com.flipperdevices.protobuf.storage.deleteRequest
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.listRequest
import com.flipperdevices.protobuf.storage.mkdirRequest
import com.flipperdevices.protobuf.storage.writeRequest
import com.google.protobuf.ByteString
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import java.io.File

class FileManagerViewModel @AssistedInject constructor(
    private val serviceProvider: FlipperServiceProvider,
    @Assisted
    private val directory: String
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "FileManagerViewModel"

    private val mutex = Mutex()
    private var fileManagerStateFlow =
        MutableStateFlow(FileManagerState(currentPath = directory))

    init {
        serviceProvider.provideServiceApi(this) {
            launchWithLock(mutex, viewModelScope, "first_invalidate") {
                invalidate(it.requestApi)
            }
        }
    }

    fun getFileManagerState(): StateFlow<FileManagerState> = fileManagerStateFlow

    fun onCreateAction(
        createFileManagerAction: CreateFileManagerAction,
        name: String
    ) {
        fileManagerStateFlow.update { FileManagerState(currentPath = directory) }
        serviceProvider.provideServiceApi(this) { serviceApi ->
            launchWithLock(mutex, viewModelScope, "create") {
                fileManagerStateFlow.emit(FileManagerState(currentPath = directory))
                serviceApi.requestApi.request(
                    getCreateRequest(createFileManagerAction, name)
                ).collect()
                invalidate(serviceApi.requestApi)
            }
        }
    }

    fun onDeleteAction(fileItem: FileItem) {
        fileManagerStateFlow.update { FileManagerState(currentPath = directory) }
        serviceProvider.provideServiceApi(this) { serviceApi ->
            launchWithLock(mutex, viewModelScope, "delete") {
                fileManagerStateFlow.emit(FileManagerState(currentPath = directory))
                serviceApi.requestApi.request(
                    main {
                        storageDeleteRequest = deleteRequest {
                            path = fileItem.path
                            recursive = true
                        }
                    }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
                ).collect()
                invalidate(serviceApi.requestApi)
            }
        }
    }

    private suspend fun invalidate(requestApi: FlipperRequestApi) {
        requestApi.request(
            main {
                storageListRequest = listRequest {
                    path = directory
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
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
                oldState.copy(filesInDirectory = newSet.toImmutableSet())
            }
        }
        fileManagerStateFlow.update {
            it.copy(inProgress = false)
        }
    }

    private fun getCreateRequest(
        createFileManagerAction: CreateFileManagerAction,
        name: String
    ): FlipperRequest {
        val absolutePath = File(directory, name).absolutePath
        return main {
            hasNext = false
            when (createFileManagerAction) {
                CreateFileManagerAction.FILE -> storageWriteRequest = writeRequest {
                    path = absolutePath
                    file = file { data = ByteString.EMPTY }
                }

                CreateFileManagerAction.FOLDER -> storageMkdirRequest = mkdirRequest {
                    path = absolutePath
                }
            }
        }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            directory: String
        ): FileManagerViewModel
    }
}
