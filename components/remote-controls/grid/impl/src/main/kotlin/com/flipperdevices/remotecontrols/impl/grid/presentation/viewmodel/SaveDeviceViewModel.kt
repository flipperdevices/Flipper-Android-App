package com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import dagger.assisted.Assisted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveDeviceViewModel @Inject constructor(
    private val updateKeyApi: UpdateKeyApi,
    private val simpleKeyApi: SimpleKeyApi,
    private val synchronizationApi: SynchronizationApi,
) : DecomposeViewModel() {
    val synchronizationState = synchronizationApi.getSynchronizationState()

    fun saveToDatabase(
        remotesPath: FlipperFilePath,
        uiPath: FlipperFilePath,
        rawRemotes: String,
        rawUi: String
    ) {
        viewModelScope.launch {
            val remotesContent = FlipperKeyContent.RawData(rawRemotes.toByteArray())
            val uiContent = FlipperKeyContent.RawData(rawUi.toByteArray())
            val flipperKeyPath = FlipperKeyPath(
                path = remotesPath,
                deleted = false
            )
            val additionalFiles = listOf(
                FlipperFile(
                    path = uiPath,
                    content = uiContent
                )
            )
            val existingKey = simpleKeyApi.getKey(flipperKeyPath)
            val key = existingKey ?: FlipperKey(
                mainFile = FlipperFile(
                    path = remotesPath,
                    content = remotesContent
                ),
                synchronized = true,
                deleted = false,
            )
            if (existingKey == null) simpleKeyApi.insertKey(key = key)
            updateKeyApi.updateKey(key, key.copy(additionalFiles = additionalFiles))
            synchronizationApi.startSynchronization(force = true)
        }
    }
}