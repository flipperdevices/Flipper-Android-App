package com.flipperdevices.nfc.mfkey32.screen.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import com.flipperdevices.nfc.mfkey32.screen.model.ErrorType
import com.flipperdevices.nfc.mfkey32.screen.model.FoundedInformation
import com.flipperdevices.nfc.mfkey32.screen.model.FoundedKey
import com.flipperdevices.nfc.mfkey32.screen.model.MfKey32State
import com.flipperdevices.nfc.tools.api.MfKey32Nonce
import com.flipperdevices.nfc.tools.api.NfcToolsApi
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import java.io.FileNotFoundException
import java.util.concurrent.Executors
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

const val PATH_NONCE_LOG = "/ext/nfc/.mfkey32.log"
private const val TOTAL_PERCENT = 1.0f

class MfKey32ViewModel @VMInject constructor(
    context: Context,
    private val nfcToolsApi: NfcToolsApi,
    private val mfKey32Api: MfKey32Api,
    private val metricApi: MetricApi,
    flipperServiceProvider: FlipperServiceProvider
) : LifecycleViewModel(), LogTagProvider, FlipperBleServiceConsumer {
    override val TAG = "MfKey32ViewModel"
    private val bruteforceDispatcher = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    ).asCoroutineDispatcher()
    private val mfKey32StateFlow = MutableStateFlow<MfKey32State>(
        MfKey32State.DownloadingRawFile(null)
    )

    private val existedKeysStorage = ExistedKeysStorage(context)
    private val fileWithNonce by lazy {
        FlipperStorageProvider.getTemporaryFile(context)
    }

    init {
        flipperServiceProvider.provideServiceApi(this, this)
    }

    fun getMfKey32State(): StateFlow<MfKey32State> = mfKey32StateFlow
    fun getFoundedInformation(): StateFlow<FoundedInformation> =
        existedKeysStorage.getFoundedInformation()

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch(Dispatchers.Default) {
            if (!prepare(serviceApi)) {
                return@launch
            }

            val nonces = KeyNonceParser.parse(fileWithNonce.readText())
            mfKey32StateFlow.emit(MfKey32State.Calculating(0f))
            nonces.map { nonce ->
                async(bruteforceDispatcher) {
                    val key = nfcToolsApi.bruteforceKey(nonce)
                    info { "Key for nonce $nonce = $key" }
                    onFoundKey(nonce, key, nonces.size)
                }
            }.forEach {
                it.await()
            }
            mfKey32StateFlow.emit(MfKey32State.Uploading)
            val addedKeys = try {
                existedKeysStorage.upload(serviceApi.requestApi)
            } catch (exception: Throwable) {
                error(exception) { "When save keys" }
                mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.READ_WRITE))
                return@launch
            }
            deleteBruteforceApp(serviceApi.requestApi)
            mfKey32StateFlow.emit(MfKey32State.Saved(addedKeys.toImmutableList()))
        }
    }

    private suspend fun prepare(serviceApi: FlipperServiceApi): Boolean {
        val connectionState = serviceApi.connectionInformationApi.getConnectionStateFlow().first()

        if (connectionState !is ConnectionState.Ready ||
            connectionState.supportedState != FlipperSupportedState.READY
        ) {
            error { "Flipper not connected" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.FLIPPER_CONNECTION))
            return false
        }

        try {
            DownloadFileHelper.downloadFile(
                serviceApi.requestApi,
                PATH_NONCE_LOG,
                fileWithNonce
            ) {
                info { "Download file progress $it" }
            }
        } catch (notFoundException: FileNotFoundException) {
            error(notFoundException) { "Not found $PATH_NONCE_LOG" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.NOT_FOUND_FILE))
            return false
        }
        metricApi.reportSimpleEvent(SimpleEvent.MFKEY32)
        try {
            existedKeysStorage.load(serviceApi.requestApi)
        } catch (exception: Throwable) {
            error(exception) { "When load keys" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.READ_WRITE))
            return false
        }
        info { "File download sucs" }

        return true
    }

    private suspend fun deleteBruteforceApp(requestApi: FlipperRequestApi) {
        requestApi.request(
            main {
                storageDeleteRequest = deleteRequest {
                    path = PATH_NONCE_LOG
                }
            }.wrapToRequest()
        ).collect()
        mfKey32Api.checkBruteforceFileExist(requestApi)
    }

    private suspend fun onFoundKey(nonce: MfKey32Nonce, key: ULong?, totalCount: Int) {
        val perNoncePercent = TOTAL_PERCENT / totalCount
        mfKey32StateFlow.update {
            if (it is MfKey32State.Calculating) {
                it.copy(percent = it.percent + perNoncePercent)
            } else {
                it
            }
        }
        val foundedKey = FoundedKey(
            nonce.sectorName,
            nonce.keyName,
            "%012X".format(key)
        )
        existedKeysStorage.onNewKey(foundedKey)
    }
}
