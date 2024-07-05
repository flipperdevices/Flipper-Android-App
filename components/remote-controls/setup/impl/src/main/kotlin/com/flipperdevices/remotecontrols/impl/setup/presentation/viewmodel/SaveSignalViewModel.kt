package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

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
import com.flipperdevices.remotecontrols.api.SaveSignalApi
import com.flipperdevices.remotecontrols.impl.setup.api.save.file.SaveFileApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SaveSignalApi::class)
class SaveSignalViewModel @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val saveFileApi: SaveFileApi
) : DecomposeViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider,
    SaveSignalApi {
    private val mutex = Mutex()
    override val TAG: String = "SaveFileViewModel"
    override val state = MutableStateFlow<SaveSignalApi.State>(SaveSignalApi.State.Pending)

    override fun save(fff: FlipperFileFormat, filePath: String) {
        val deeplinkContent = DeeplinkContent.FFFContent(filePath, fff)
        val ffPath = FlipperFilePath(
            FlipperKeyType.INFRARED.flipperDir,
            filePath
        )
        launchWithLock(mutex, viewModelScope, "load") {
            val serviceApi = withContext(Dispatchers.Main) { serviceProvider.getServiceApi() }
            val saveFileFlow = saveFileApi.save(
                requestApi = serviceApi.requestApi,
                deeplinkContent = deeplinkContent,
                ffPath = ffPath
            )
            saveFileFlow
                .flowOn(FlipperDispatchers.workStealingDispatcher)
                .onEach {
                    when (it) {
                        SaveFileApi.Status.Finished -> SaveSignalApi.State.Uploaded
                        is SaveFileApi.Status.Saving -> SaveSignalApi.State.Uploading(
                            it.uploaded,
                            it.size
                        )
                    }
                }
                .launchIn(this)
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit
}
