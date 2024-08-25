package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
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
            var progressInternal = 0L
            val totalSize = filesDesc.sumOf { it.textContent.toByteArray().size.toLong() }
            _state.emit(SaveTempSignalApi.State.Uploading(0, totalSize))
            val serviceApi = serviceProvider.getServiceApi()
            launchWithLock(mutex, viewModelScope, "load") {
                filesDesc.forEach { fileDesc ->
                    val absolutePath = FlipperFilePath(
                        folder = fileDesc.extFolderPath,
                        nameWithExtension = fileDesc.nameWithExtension
                    ).getPathOnFlipper()

                    saveFolderApi.save(serviceApi.requestApi, "$EXT_PATH/${fileDesc.extFolderPath}")
                    val saveFileFlow = saveFileApi.save(
                        requestApi = serviceApi.requestApi,
                        textContent = fileDesc.textContent,
                        absolutePath = absolutePath
                    )
                    saveFileFlow
                        .flowOn(FlipperDispatchers.workStealingDispatcher)
                        .onEach {
                            _state.value = when (it) {
                                SaveFileApi.Status.Finished -> SaveTempSignalApi.State.Uploaded
                                is SaveFileApi.Status.Saving -> {
                                    progressInternal += it.lastWriteSize
                                    SaveTempSignalApi.State.Uploading(
                                        progressInternal,
                                        totalSize
                                    )
                                }
                            }
                        }
                        .collect()
                }
                _state.value = SaveTempSignalApi.State.Uploaded
                onFinished.invoke()
            }
        }
    }
}
