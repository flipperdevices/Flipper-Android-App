package com.flipperdevices.filemanager.impl.viewmodels

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.toThrowableFlow
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.impl.model.CreateFileManagerAction
import com.flipperdevices.filemanager.impl.model.EditorState
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.filemanager.impl.model.FileManagerState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import okio.ByteString
import okio.buffer
import kotlin.io.path.Path
import java.io.File

class FileManagerViewModel @AssistedInject constructor(
    private val featureProvider: FFeatureProvider,
    @Assisted private val directory: String
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "FileManagerViewModel"

    private val mutex = Mutex()
    private val fileManagerStateFlow = MutableStateFlow<FileManagerState>(
        FileManagerState.Ready(
            currentPath = directory, inProgress = true
        )
    )

    init {
        viewModelScope.launch {
            featureProvider.get<FStorageFeatureApi>().collectLatest {
                invalidate(it)
            }
        }
    }

    fun getFileManagerState(): StateFlow<FileManagerState> = fileManagerStateFlow

    fun onCreateAction(
        createFileManagerAction: CreateFileManagerAction, name: String
    ) {
        fileManagerStateFlow.update {
            FileManagerState.Ready(
                currentPath = directory,
                inProgress = true
            )
        }

        launchWithLock(mutex, viewModelScope, "create") {
            fileManagerStateFlow.emit(
                FileManagerState.Ready(
                    currentPath = directory, inProgress = true
                )
            )
            val storageApi = featureProvider.getSync<FStorageFeatureApi>()
            if (storageApi == null) {
                fileManagerStateFlow.emit(FileManagerState.Error)
                return@launchWithLock
            }
            val uploadApi = storageApi.uploadApi()
            val pathOnFlipper = Path(directory).resolve(name).toString()
            when (createFileManagerAction) {
                CreateFileManagerAction.FILE -> runCatching {
                    uploadApi.sink(
                        pathOnFlipper = pathOnFlipper,
                        priority = StorageRequestPriority.FOREGROUND
                    ).buffer().use {
                        it.write(ByteString.of())
                    }
                }

                CreateFileManagerAction.FOLDER -> uploadApi.mkdir(
                    pathOnFlipper
                )
            }.onFailure {
                error(it) { "Fail create $name $createFileManagerAction" }
                fileManagerStateFlow.emit(FileManagerState.Error)
            }.onSuccess {
                listFiles(storageApi.listingApi())
            }
        }

    }

    fun onDeleteAction(fileItem: FileItem) {
        fileManagerStateFlow.value = FileManagerState.Ready(
            currentPath = directory, inProgress = true
        )

        launchWithLock(mutex, viewModelScope, "delete") {
            fileManagerStateFlow.emit(
                FileManagerState.Ready(
                    currentPath = directory, inProgress = true
                )
            )
            val storageApi = featureProvider.getSync<FStorageFeatureApi>()
            if (storageApi == null) {
                fileManagerStateFlow.emit(FileManagerState.Error)
                return@launchWithLock
            }
            storageApi.deleteApi().delete(fileItem.path, recursive = true)
                .onSuccess { listFiles(storageApi.listingApi()) }
                .onFailure { fileManagerStateFlow.emit(FileManagerState.Error) }
        }
    }

    private suspend fun invalidate(
        featureStatus: FFeatureStatus<FStorageFeatureApi>
    ) = withLock(mutex, "invalidate") {
        when (featureStatus) {
            FFeatureStatus.NotFound, FFeatureStatus.Unsupported -> fileManagerStateFlow.emit(
                FileManagerState.Error
            )

            FFeatureStatus.Retrieving -> fileManagerStateFlow.emit(
                FileManagerState.Ready(
                    currentPath = directory, inProgress = true
                )
            )

            is FFeatureStatus.Supported -> listFiles(featureStatus.featureApi.listingApi())
        }
    }

    private suspend fun listFiles(
        listingApi: FListingStorageApi
    ) {
        listingApi.lsFlow(directory).toThrowableFlow().catch {
            fileManagerStateFlow.emit(FileManagerState.Error)
        }.collect { listingItems ->
            fileManagerStateFlow.update { oldState ->
                if (oldState is FileManagerState.Ready) {
                    oldState.copy(
                        filesInDirectory = oldState.filesInDirectory.plus(listingItems.map { item ->
                            FileItem(
                                directory, item
                            )
                        }).toPersistentList()
                    )
                } else oldState
            }
        }

        fileManagerStateFlow.update { oldState ->
            if (oldState is FileManagerState.Ready) {
                oldState.copy(
                    inProgress = false
                )
            } else oldState
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            directory: String
        ): FileManagerViewModel
    }
}
