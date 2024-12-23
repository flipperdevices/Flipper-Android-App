package com.flipperdevices.nfc.mfkey32.screen.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDeleteApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileStorageMD5Api
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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import okio.Path.Companion.toOkioPath
import java.math.BigInteger
import java.util.concurrent.Executors
import javax.inject.Inject

const val PATH_NONCE_LOG = "/ext/nfc/.mfkey32.log"
private const val TOTAL_PERCENT = 1.0f

class MfKey32ViewModel @Inject constructor(
    private val nfcToolsApi: NfcToolsApi,
    private val mfKey32Api: MfKey32Api,
    private val metricApi: MetricApi,
    private val fFeatureProvider: FFeatureProvider,
    storageProvider: FlipperStorageProvider
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "MfKey32ViewModel"
    private val bruteforceDispatcher = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    ).asCoroutineDispatcher()
    private val mfKey32StateFlow = MutableStateFlow<MfKey32State>(
        MfKey32State.Error(ErrorType.FLIPPER_CONNECTION)
    )

    private val existedKeysStorage = ExistedKeysStorage(storageProvider)
    private val fileWithNonce by lazy {
        storageProvider.getTemporaryFile().toFile()
    }

    fun getMfKey32State(): StateFlow<MfKey32State> = mfKey32StateFlow
    fun getFoundedInformation(): StateFlow<FoundedInformation> =
        existedKeysStorage.getFoundedInformation()

    init {
        fFeatureProvider
            .get<FStorageFeatureApi>()
            .onEach { status -> startCalculation(status) }
            .launchIn(viewModelScope)
    }

    private suspend fun startCalculation(status: FFeatureStatus<FStorageFeatureApi>) {
        info { "Start calculation on $status" }

        val featureApi = when (status) {
            FFeatureStatus.Unsupported,
            FFeatureStatus.NotFound -> {
                mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.FLIPPER_CONNECTION))
                return
            }

            FFeatureStatus.Retrieving -> {
                mfKey32StateFlow.emit(MfKey32State.WaitingForFlipper)
                return
            }

            is FFeatureStatus.Supported -> {
                status.featureApi
            }
        }

        if (!prepare(featureApi.downloadApi(), featureApi.md5Api())) {
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
            existedKeysStorage.upload(featureApi.uploadApi())
        } catch (exception: Throwable) {
            error(exception) { "When save keys" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.READ_WRITE))
            return
        }
        deleteBruteforceApp(
            deleteApi = featureApi.deleteApi(),
            md5Api = featureApi.md5Api()
        )
        mfKey32StateFlow.emit(MfKey32State.Saved(addedKeys.toImmutableList()))
    }

    private suspend fun prepare(
        fFileDownloadApi: FFileDownloadApi,
        md5Api: FFileStorageMD5Api
    ): Boolean {
        info { "Flipper connected" }

        if (!mfKey32Api.isBruteforceFileExist) {
            info { "Not found $PATH_NONCE_LOG" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.NOT_FOUND_FILE))
        }

        mfKey32Api.checkBruteforceFileExist(md5Api)

        if (!mfKey32Api.isBruteforceFileExist) {
            return false
        }

        mfKey32StateFlow.emit(MfKey32State.DownloadingRawFile(0f))

        try {
            fFileDownloadApi.download(
                pathOnFlipper = PATH_NONCE_LOG,
                fileOnAndroid = fileWithNonce.toOkioPath(),
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
            existedKeysStorage.load(fFileDownloadApi)
        } catch (exception: Throwable) {
            error(exception) { "When load keys" }
            mfKey32StateFlow.emit(MfKey32State.Error(ErrorType.READ_WRITE))
            return false
        }
        info { "File download sucs" }

        return true
    }

    private suspend fun deleteBruteforceApp(
        deleteApi: FFileDeleteApi,
        md5Api: FFileStorageMD5Api
    ) {
        deleteApi.delete(path = PATH_NONCE_LOG)
            .onFailure { error(it) { "#deleteBruteforceApp could not delete " } }
        mfKey32Api.checkBruteforceFileExist(md5Api)
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
