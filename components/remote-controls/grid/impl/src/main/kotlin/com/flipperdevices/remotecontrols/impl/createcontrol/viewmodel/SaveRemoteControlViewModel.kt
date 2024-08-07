package com.flipperdevices.remotecontrols.impl.createcontrol.viewmodel

import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.renameRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveRemoteControlViewModel @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val updateKeyApi: UpdateKeyApi,
    private val simpleKeyApi: SimpleKeyApi
) : DecomposeViewModel() {
    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    private suspend fun move(
        oldPath: FlipperFilePath,
        newPath: FlipperFilePath,
    ) {
        val serviceApi = flipperServiceProvider.getServiceApi()
        val moveRequest = main {
            storageRenameRequest = renameRequest {
                this.oldPath = oldPath.getPathOnFlipper()
                this.newPath = newPath.getPathOnFlipper()
            }
        }.wrapToRequest()
        serviceApi.requestApi.request(moveRequest).collect()
    }

    private fun FlipperFilePath.toNonTempPath() = copy(folder = folder.replace("/temp", ""))

    fun moveAndUpdate(
        savedKeyPath: FlipperKeyPath,
        originalKey: NotSavedFlipperKey,
    ) {
        viewModelScope.launch {
            val flipperKey = simpleKeyApi.getKey(savedKeyPath) ?: run {
                _state.emit(State.KeyNotFound)
                return@launch
            }
            _state.emit(State.Updating)
            move(
                oldPath = flipperKey.mainFile.path,
                newPath = flipperKey.mainFile.path.toNonTempPath()
            )
            originalKey.additionalFiles.forEach {
                move(
                    oldPath = it.path,
                    newPath = it.path.toNonTempPath()
                )
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
                    }
                )
            )
            val keyPath = FlipperKeyPath(
                path = flipperKey.mainFile.path.toNonTempPath(),
                deleted = false
            )
            _state.emit(State.Finished(keyPath))
        }
    }

    sealed interface State {
        data object Pending : State
        data object Updating : State
        data class Finished(val keyPath: FlipperKeyPath) : State
        data object KeyNotFound : State
    }
}