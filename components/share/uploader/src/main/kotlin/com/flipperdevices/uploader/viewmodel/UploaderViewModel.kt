package com.flipperdevices.uploader.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.uploader.R
import com.flipperdevices.uploader.api.EXTRA_KEY_PATH
import com.flipperdevices.uploader.models.ShareContent
import com.flipperdevices.uploader.models.ShareError
import com.flipperdevices.uploader.models.ShareState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject
import java.net.UnknownHostException
import java.net.UnknownServiceException

private const val SHORT_LINK_SIZE = 256

class UploaderViewModel @VMInject constructor(
    private val keyParser: KeyParser,
    private val cryptoStorageApi: CryptoStorageApi,
    private val simpleKeyApi: SimpleKeyApi,
    private val metricApi: MetricApi,
    @TangleParam(EXTRA_KEY_PATH)
    private val flipperKeyPath: FlipperKeyPath?
) : LifecycleViewModel(), LogTagProvider {
    override val TAG: String = "UploaderViewModel"

    private val _state = MutableStateFlow<ShareState>(ShareState.Initial)
    fun getState() = _state.asStateFlow()
    fun getFlipperKeyName() = flipperKeyPath?.path?.nameWithExtension ?: ""

    init {
        viewModelScope.launch { parseFlipperKeyPath() }
    }

    private suspend fun parseFlipperKeyPath() {
        if (flipperKeyPath == null) {
            _state.emit(ShareState.Completed)
            return
        }
        simpleKeyApi.getKeyAsFlow(flipperKeyPath).collectLatest { flipperKey ->
            if (flipperKey == null) {
                _state.emit(ShareState.Error(ShareError.OTHER))
                return@collectLatest
            }
            prepareParseKeyContent(flipperKey)
        }
    }

    private suspend fun prepareParseKeyContent(flipperKey: FlipperKey) {
        val shortLink = keyParser.keyToUrl(flipperKey)
        val contentLink = if (shortLink.length <= SHORT_LINK_SIZE) shortLink else null
        _state.emit(
            ShareState.PendingShare(
                content = ShareContent(
                    link = contentLink,
                    flipperKey = flipperKey
                )
            )
        )
    }

    fun shareByFile(content: ShareContent, context: Context) {
        viewModelScope.launch {
            runCatching {
                val data = extractKeyContentForShare(content.flipperKey).openStream()
                    .use { it.readBytes() }
                metricApi.reportSimpleEvent(SimpleEvent.SHARE_FILE)
                ShareHelper.shareRawFile(
                    context = context,
                    data = data,
                    resId = R.string.share_file,
                    name = getFlipperKeyName()
                )
                _state.emit(ShareState.Completed)
            }.onFailure { exception ->
                error(exception) { "Error on share $flipperKeyPath by file" }
                _state.emit(ShareState.Error(ShareError.OTHER))
            }
        }
    }

    fun shareViaLink(content: ShareContent, context: Context) {
        viewModelScope.launch {
            val contentLink = content.link
            if (contentLink != null) {
                metricApi.reportSimpleEvent(SimpleEvent.SHARE_SHORT_LINK)
                ShareHelper.shareText(
                    context = context,
                    title = getFlipperKeyName(),
                    text = contentLink
                )
                _state.emit(ShareState.Completed)
            } else {
                _state.emit(ShareState.Prepare)
                shareLongLink(flipperKey = content.flipperKey, context = context)
            }
        }
    }

    private suspend fun shareLongLink(flipperKey: FlipperKey, context: Context) {
        val uploadedLink = cryptoStorageApi.upload(
            keyContent = extractKeyContentForShare(flipperKey),
            path = flipperKey.path.pathToKey,
            name = flipperKey.mainFile.path.nameWithExtension
        )
        uploadedLink.onSuccess { link ->
            metricApi.reportSimpleEvent(SimpleEvent.SHARE_LONG_LINK)
            ShareHelper.shareText(
                context = context,
                title = getFlipperKeyName(),
                text = link
            )
            _state.emit(ShareState.Completed)
        }
        uploadedLink.onFailure { exception ->
            error(exception) { "Error on upload $flipperKey to server" }
            val error = when (exception) {
                is UnknownHostException -> ShareError.NO_INTERNET_CONNECTION
                is UnknownServiceException -> ShareError.CANT_CONNECT_TO_SERVER
                else -> ShareError.OTHER
            }
            _state.emit(ShareState.Error(typeError = error))
        }
    }

    fun retryShare() {
        viewModelScope.launch {
            _state.emit(ShareState.Initial)
            parseFlipperKeyPath()
        }
    }

    private fun extractKeyContentForShare(flipperKey: FlipperKey): FlipperKeyContent {
        val shadowFile = flipperKey
            .additionalFiles
            .firstOrNull { it.path.fileType == FlipperFileType.SHADOW_NFC }
        if (flipperKey.flipperKeyType == FlipperKeyType.NFC && shadowFile != null) {
            return shadowFile.content
        }

        return flipperKey.mainFile.content
    }
}
