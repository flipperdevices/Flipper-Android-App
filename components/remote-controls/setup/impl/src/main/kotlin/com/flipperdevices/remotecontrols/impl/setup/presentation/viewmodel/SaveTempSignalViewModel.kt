package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.impl.setup.api.save.file.SaveFileApi
import com.flipperdevices.remotecontrols.impl.setup.api.save.folder.SaveFolderApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

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


    override fun saveTempFile(fff: FlipperFileFormat, nameWithExtension: String) {
        launchWithLock(mutex, viewModelScope, "load") {
            val serviceApi = withContext(Dispatchers.Main) { serviceProvider.getServiceApi() }
            saveFolderApi.save(serviceApi.requestApi, "/ext/infrared/temp")
            save(
                serviceApi = serviceApi,
                fff = fff,
                ffPath = FlipperFilePath(
                    folder = FlipperKeyType.INFRARED.flipperDir + "/temp",
                    nameWithExtension = nameWithExtension
                )
            )
        }
    }

    private suspend fun save(
        serviceApi: FlipperServiceApi,
        fff: FlipperFileFormat,
        ffPath: FlipperFilePath
    ) = coroutineScope {
        val deeplinkContent = DeeplinkContent.FFFContent(ffPath.nameWithExtension, fff)
        val saveFileFlow = saveFileApi.save(
            requestApi = serviceApi.requestApi,
            deeplinkContent = deeplinkContent,
            absolutePath = ffPath.getPathOnFlipper()
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
            .launchIn(this)
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit
}
