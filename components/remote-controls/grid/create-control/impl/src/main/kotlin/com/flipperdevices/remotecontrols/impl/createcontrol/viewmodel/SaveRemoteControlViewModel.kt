package com.flipperdevices.remotecontrols.impl.createcontrol.viewmodel

import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyedit.api.NotSavedFlipperFile
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.flipperdevices.protobuf.storage.renameRequest
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveRemoteControlViewModel @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val updateKeyApi: UpdateKeyApi,
    private val simpleKeyApi: SimpleKeyApi,
    private val synchronizationApi: SynchronizationApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "SaveRemoteControlViewModel"

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    private var lastMoveJob: Job? = null

    private fun FlipperFilePath.toNonTempPath() = copy(folder = folder.replace("/temp", ""))

    private suspend fun delete(path: FlipperFilePath) {
        val serviceApi = flipperServiceProvider.getServiceApi()
        val moveResponse = serviceApi.requestApi.request(
            command = main {
                storageDeleteRequest = deleteRequest {
                    this.path = path.getPathOnFlipper()
                }
            }.wrapToRequest()
        ).first()
        if (moveResponse.commandStatus != Flipper.CommandStatus.OK) {
            error("Could not delete file ${path.getPathOnFlipper()} status: ${moveResponse.commandStatus}")
        }
    }

    private suspend fun move(
        oldPath: FlipperFilePath,
        newPath: FlipperFilePath,
    ) {
        val serviceApi = flipperServiceProvider.getServiceApi()
        val moveResponse = serviceApi.requestApi.request(
            command = main {
                storageRenameRequest = renameRequest {
                    this.oldPath = oldPath.getPathOnFlipper()
                    this.newPath = newPath.getPathOnFlipper()
                }
            }.wrapToRequest()
        ).first()
        if (moveResponse.commandStatus != Flipper.CommandStatus.OK) {
            error("Could not move file ${oldPath.getPathOnFlipper()} status: ${moveResponse.commandStatus}")
        }
    }

    private suspend fun awaitSynchronization(
        onChange: (progress: Float) -> Unit
    ): Unit = coroutineScope {
        if (!synchronizationApi.isSynchronizationRunning()) {
            synchronizationApi.startSynchronization(force = true)
        }
        val progressJon = synchronizationApi.getSynchronizationState()
            .filterIsInstance<SynchronizationState.InProgress>()
            .onEach { onChange.invoke(it.progress) }
            .launchIn(this)
        synchronizationApi.getSynchronizationState()
            .filterIsInstance<SynchronizationState.InProgress>()
            .onEach { onChange.invoke(it.progress) }
            .first()
        synchronizationApi.getSynchronizationState()
            .filterIsInstance<SynchronizationState.Finished>()
            .first()
        progressJon.cancelAndJoin()
    }

    /**
     * Move files to new location and delete temp files
     * @return true if successful false if failure met
     */
    private suspend fun tryModifyFiles(
        originalKey: NotSavedFlipperKey,
        flipperKey: FlipperKey
    ): Boolean {
        val result = runCatching {
            move(
                oldPath = originalKey.mainFile.path,
                newPath = flipperKey.mainFile.path.toNonTempPath()
            )
            delete(originalKey.mainFile.path)
            originalKey.additionalFiles
                .map(NotSavedFlipperFile::path)
                .forEach { path ->
                    move(
                        oldPath = path,
                        newPath = path.toNonTempPath()
                            .copyWithChangedName(flipperKey.mainFile.path.nameWithoutExtension)
                    )
                    delete(path)
                }
        }
        return result.isSuccess
    }

    fun moveAndUpdate(
        savedKeyPath: FlipperKeyPath,
        originalKey: NotSavedFlipperKey,
    ) {
        viewModelScope.launch {
            _state.emit(State.InProgress.ModifyingFiles)
            if (lastMoveJob != null) lastMoveJob?.join()
            lastMoveJob = coroutineContext.job

            val flipperKey = simpleKeyApi.getKey(savedKeyPath) ?: run {
                _state.emit(State.KeyNotFound)
                return@launch
            }
            if (!tryModifyFiles(originalKey, flipperKey)) {
                _state.emit(State.CouldNotModifyFiles)
                return@launch
            }

            updateKeyApi.updateKey(
                oldKey = flipperKey,
                newKey = flipperKey.copy(
                    mainFile = flipperKey.mainFile.copy(
                        path = flipperKey.mainFile.path.toNonTempPath()
                    ),
                    additionalFiles = originalKey.additionalFiles.map {
                        FlipperFile(
                            path = it.path.toNonTempPath(),
                            content = it.content
                        )
                    }.toImmutableList()
                )
            )
            awaitSynchronization(onChange = { _state.value = State.InProgress.Synchronizing(it) })
            val keyPath = FlipperKeyPath(
                path = flipperKey.mainFile.path.toNonTempPath(),
                deleted = false
            )
            _state.emit(State.Finished(keyPath))
        }
    }

    sealed interface State {
        data object Pending : State
        sealed interface InProgress : State {
            data object ModifyingFiles : InProgress
            data class Synchronizing(val progress: Float) : InProgress
        }

        data class Finished(val keyPath: FlipperKeyPath) : State
        data object KeyNotFound : State
        data object CouldNotModifyFiles : State
    }
}
