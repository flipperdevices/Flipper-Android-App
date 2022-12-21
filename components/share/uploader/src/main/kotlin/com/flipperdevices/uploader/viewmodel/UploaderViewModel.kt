package com.flipperdevices.uploader.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.api.ShareContentError
import com.flipperdevices.share.uploader.R
import com.flipperdevices.uploader.api.EXTRA_KEY_PATH
import com.flipperdevices.uploader.models.ShareContent
import com.flipperdevices.uploader.models.ShareState
import java.net.UnknownHostException
import java.net.UnknownServiceException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

private const val SHORT_LINK_SIZE = 200

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
                _state.emit(ShareState.Error(ShareContentError.OTHER))
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
                val data = content.flipperKey.keyContent.openStream().use { it.readBytes() }
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
                _state.emit(ShareState.Error(ShareContentError.OTHER))
            }
        }
    }

    fun shareViaLink(content: ShareContent, context: Context) {
        viewModelScope.launch {
            val contentLink = content.link
            if (contentLink != null) {
                metricApi.reportSimpleEvent(SimpleEvent.SHARE_SHORTLINK)
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
        val shadowFile = flipperKey
            .additionalFiles
            .firstOrNull { it.path.fileType == FlipperFileType.SHADOW_NFC }
        val flipperKeyContent = (shadowFile ?: flipperKey.mainFile)
            .content
            .openStream()
            .use { it.readBytes() }

        val uploadedLink = cryptoStorageApi.upload(
            data = flipperKeyContent,
            path = flipperKey.path.pathToKey,
            name = flipperKey.mainFile.path.nameWithExtension
        )
        uploadedLink.onSuccess { link ->
            metricApi.reportSimpleEvent(SimpleEvent.SHARE_LONGLINK)
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
                is UnknownHostException -> ShareContentError.NO_INTERNET
                is UnknownServiceException -> ShareContentError.SERVER_ERROR
                else -> ShareContentError.OTHER
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
}
