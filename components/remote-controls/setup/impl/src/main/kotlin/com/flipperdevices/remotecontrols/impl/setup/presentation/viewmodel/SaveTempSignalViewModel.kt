package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.preference.FlipperStorageProvider
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
    private val flipperStorageApi: FlipperStorageApi,
    private val context: Context
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
            _state.emit(SaveTempSignalApi.State.Uploading(0f))
            launchWithLock(mutex, viewModelScope, "load") {
                filesDesc.forEachIndexed { index, fileDesc ->
                    FlipperStorageProvider.useTemporaryFile(context) { deviceFile ->
                        deviceFile.writeText(fileDesc.textContent)
                        val fAbsolutePath = FlipperFilePath(
                            folder = fileDesc.extFolderPath,
                            nameWithExtension = fileDesc.nameWithExtension
                        ).getPathOnFlipper()
                        flipperStorageApi.mkdirs("$EXT_PATH/${fileDesc.extFolderPath}")
                        flipperStorageApi.upload(
                            pathOnFlipper = fAbsolutePath,
                            fileOnAndroid = deviceFile,
                            progressListener = { currentProgress ->
                                _state.value = SaveTempSignalApi.State.Uploading(
                                    progressPercent = index + currentProgress
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
