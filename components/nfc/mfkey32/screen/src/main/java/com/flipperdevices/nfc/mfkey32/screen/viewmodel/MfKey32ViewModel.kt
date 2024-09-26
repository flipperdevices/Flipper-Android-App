package com.flipperdevices.nfc.mfkey32.screen.viewmodel

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.math.BigInteger
import java.util.concurrent.Executors
import javax.inject.Inject

const val PATH_NONCE_LOG = "/ext/nfc/.mfkey32.log"
private const val TOTAL_PERCENT = 1.0f

class MfKey32ViewModel @Inject constructor(
    private val nfcToolsApi: NfcToolsApi,
    private val mfKey32Api: MfKey32Api,
    private val metricApi: MetricApi,
    flipperServiceProvider: FlipperServiceProvider,
    private val flipperStorageApi: FlipperStorageApi,
    storageProvider: FlipperStorageProvider
) : DecomposeViewModel(), LogTagProvider, FlipperBleServiceConsumer {
    override val TAG = "MfKey32ViewModel"
    private val bruteforceDispatcher = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    ).asCoroutineDispatcher()
    private val mfKey32StateFlow = MutableStateFlow<MfKey32State>(
        MfKey32State.Error(ErrorType.FLIPPER_CONNECTION)
    )

    private val existedKeysStorage = ExistedKeysStorage(flipperStorageApi, storageProvider)
    private val fileWithNonce by lazy {
        storageProvider.getTemporaryFile().toFile()
    }
    private var stateJob: Job? = null
    private val mutex = Mutex()

    init {
        flipperServiceProvider.provideServiceApi(this, this)
    }

    fun getMfKey32State(): StateFlow<MfKey32State> = mfKey32StateFlow
    fun getFoundedInformation(): StateFlow<FoundedInformation> =
        existedKeysStorage.getFoundedInformation()

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch {
            mutex.withLock {
                val localJob = stateJob
                stateJob = viewModelScope.launch {
                    localJob?.cancelAndJoin()
                    serviceApi
                        .connectionInformationApi
                        .getConnectionStateFlow()
                        .collectLatest { connectionState ->
                            startCalculation(serviceApi, connectionState)
                        }
                }
            }
        }
    }

    private suspend fun startCalculation(
        serviceApi: FlipperServiceApi,
        connectionState: ConnectionState
    ) {
        info { "Start calculation on $connectionState" }

        when (connectionState) {
            ConnectionState.Connecting,
            ConnectionState.Disconnecting,
            ConnectionState.RetrievingInformation,
            ConnectionState.Initializing -> {
                mfKey32StateFlow.emit(MfKey32State.WaitingForFlipper)
                return
            }

            is ConnectionState.Disconnected -> {
                mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.FLIPPER_CONNECTION))
                return
            }

            is ConnectionState.Ready -> {}
        }

        if (!prepare(serviceApi)) {
            info { "Failed prepare" }
            return
        }

        val nonces = KeyNonceParser.parse(fileWithNonce.readText())
        mfKey32StateFlow.emit(MfKey32State.Calculating(0f))
        nonces.pmap(bruteforceDispatcher) { nonce ->
            val key = nfcToolsApi.bruteforceKey(nonce)
            info { "Key for nonce $nonce = $key" }
            onFoundKey(nonce, key, nonces.size)
        }
        mfKey32StateFlow.emit(MfKey32State.Uploading)
        val addedKeys = try {
            existedKeysStorage.upload(serviceApi.requestApi)
        } catch (exception: Throwable) {
            error(exception) { "When save keys" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.READ_WRITE))
            return
        }
        deleteBruteforceApp(serviceApi.requestApi)
        mfKey32StateFlow.emit(MfKey32State.Saved(addedKeys.toImmutableList()))
    }

    private suspend fun prepare(serviceApi: FlipperServiceApi): Boolean {
        info { "Flipper connected" }

        if (!mfKey32Api.isBruteforceFileExist) {
            info { "Not found $PATH_NONCE_LOG" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.NOT_FOUND_FILE))
        }

        mfKey32Api.checkBruteforceFileExist(serviceApi.requestApi)

        if (!mfKey32Api.isBruteforceFileExist) {
            return false
        }

        mfKey32StateFlow.emit(MfKey32State.DownloadingRawFile(0f))

        try {
            flipperStorageApi.download(
                pathOnFlipper = PATH_NONCE_LOG,
                fileOnAndroid = fileWithNonce,
                progressListener = ProgressWrapperTracker(
                    progressListener = { progress ->
                        info { "Download file progress $progress" }
                        mfKey32StateFlow.emit(MfKey32State.DownloadingRawFile(progress))
                    },
                    max = 0.99f
                )
            )
        } catch (error: Throwable) {
            error(error) { "Not found $PATH_NONCE_LOG" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.NOT_FOUND_FILE))
            return false
        }
        metricApi.reportSimpleEvent(SimpleEvent.MFKEY32)
        try {
            existedKeysStorage.load()
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

    private suspend fun onFoundKey(nonce: MfKey32Nonce, key: BigInteger?, totalCount: Int) {
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
            key?.let { "%012X".format(it) }
        )
        existedKeysStorage.onNewKey(foundedKey)
    }
}
