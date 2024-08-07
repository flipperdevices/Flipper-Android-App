package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.impl.setup.api.save.file.SaveFileApi
import com.flipperdevices.remotecontrols.impl.setup.api.save.folder.SaveFolderApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

private const val EXT_PATH = "/ext"

@ContributesBinding(AppGraph::class, SaveTempSignalApi::class)
class SaveTempSignalViewModel @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val saveFileApi: SaveFileApi,
    private val saveFolderApi: SaveFolderApi,
) : DecomposeViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider,
    SaveTempSignalApi {
    private val mutex = Mutex()
    override val TAG: String = "SaveFileViewModel"
    private val _state = MutableStateFlow<SaveTempSignalApi.State>(SaveTempSignalApi.State.Pending)
    override val state = _state.asStateFlow()

    override fun saveFile(
        textContent: String,
        nameWithExtension: String,
        extFolderPath: String
    ) = save(
        extFolderPath = extFolderPath,
        textContent = textContent,
        absolutePath = FlipperFilePath(
            folder = extFolderPath,
            nameWithExtension = nameWithExtension
        ).getPathOnFlipper(),
    )

    private fun save(
        textContent: String,
        absolutePath: String,
        extFolderPath: String
    ) {
        viewModelScope.launch {
            _state.emit(SaveTempSignalApi.State.Uploading(0, 0))
            val serviceApi = serviceProvider.getServiceApi()
            launchWithLock(mutex, viewModelScope, "load") {
                saveFolderApi.save(serviceApi.requestApi, "$EXT_PATH/$extFolderPath")
                val saveFileFlow = saveFileApi.save(
                    requestApi = serviceApi.requestApi,
                    textContent = textContent,
                    absolutePath = absolutePath
                )
                saveFileFlow
                    .flowOn(FlipperDispatchers.workStealingDispatcher)
                    .onEach {
                        _state.value = when (it) {
                            SaveFileApi.Status.Finished -> SaveTempSignalApi.State.Uploaded
                            is SaveFileApi.Status.Saving -> SaveTempSignalApi.State.Uploading(
                                it.uploaded,
                                it.size
                            )
                        }
                    }
                    .collect()
                _state.value = SaveTempSignalApi.State.Uploaded
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit
}
