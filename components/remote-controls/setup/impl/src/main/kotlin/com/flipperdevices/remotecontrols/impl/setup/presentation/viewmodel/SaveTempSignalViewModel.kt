package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

private const val EXT_PATH = "/ext"

@ContributesBinding(AppGraph::class, SaveTempSignalApi::class)
class SaveTempSignalViewModel @Inject constructor(
    private val fFeatureProvider: FFeatureProvider,
    private val storageProvider: FlipperStorageProvider
) : DecomposeViewModel(),
    LogTagProvider,
    SaveTempSignalApi {
    private val mutex = Mutex()
    override val TAG: String = "SaveFileViewModel"
    private val _state = MutableStateFlow<SaveTempSignalApi.State>(SaveTempSignalApi.State.Pending)
    override val state = _state.asStateFlow()

    override fun saveFiles(
        vararg filesDesc: SaveTempSignalApi.FileDesc,
        onFinished: () -> Unit,
    ) {
        viewModelScope.launch {
            val fStorageFeatureApi = fFeatureProvider.getSync<FStorageFeatureApi>() ?: run {
                error { "#saveFiles could not get FStorageFeatureApi" }
                return@launch
            }
            _state.emit(SaveTempSignalApi.State.Uploading(0f))
            launchWithLock(mutex, viewModelScope, "load") {
                filesDesc.forEachIndexed { index, fileDesc ->
                    storageProvider.useTemporaryFile { deviceFile ->
                        deviceFile.toFile().writeText(fileDesc.textContent)
                        val fAbsolutePath = FlipperFilePath(
                            folder = fileDesc.extFolderPath,
                            nameWithExtension = fileDesc.nameWithExtension
                        ).getPathOnFlipper()
                        fStorageFeatureApi.uploadApi().mkdir("$EXT_PATH/${fileDesc.extFolderPath}")
                        fStorageFeatureApi.uploadApi().upload(
                            pathOnFlipper = fAbsolutePath,
                            fileOnAndroid = deviceFile,
                            progressListener = { current, max ->
                                _state.value = SaveTempSignalApi.State.Uploading(
                                    progressPercent = if (max == 0L) 0f else current / max.toFloat()
                                )
                            }
                        )
                    }
                }
                _state.value = SaveTempSignalApi.State.Uploaded
                onFinished.invoke()
            }
        }
    }
}
