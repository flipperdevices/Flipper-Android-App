package com.flipperdevices.remotecontrols.impl.grid.save.viewmodel

import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.renameRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveRemoteControlViewModel @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val updateKeyApi: UpdateKeyApi,
) : DecomposeViewModel() {

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
        savedKey: FlipperKey,
        originalKey: NotSavedFlipperKey,
        onFinished: (FlipperKeyPath) -> Unit
    ) {
        viewModelScope.launch {
            move(
                oldPath = savedKey.mainFile.path,
                newPath = savedKey.mainFile.path.toNonTempPath()
            )
            originalKey.additionalFiles.forEach {
                move(
                    oldPath = it.path,
                    newPath = it.path.toNonTempPath()
                )
            }

            updateKeyApi.updateKey(
                oldKey = savedKey,
                newKey = savedKey.copy(
                    mainFile = savedKey.mainFile.copy(
                        path = savedKey.mainFile.path.toNonTempPath()
                    ),
                    additionalFiles = originalKey.additionalFiles.map {
                        FlipperFile(
                            path = it.path.toNonTempPath(),
                            content = it.content
                        )
                    }
                )
            )
            withContext(Dispatchers.Main) {
                val keyPath = FlipperKeyPath(
                    path = savedKey.mainFile.path.toNonTempPath(),
                    deleted = false
                )
                onFinished.invoke(keyPath)
            }
        }
    }
}