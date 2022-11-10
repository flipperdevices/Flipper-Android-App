package com.flipperdevices.uploader.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.uploader.R
import com.flipperdevices.uploader.models.ShareContentError
import com.flipperdevices.uploader.models.UploaderState
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class UploaderViewModel @VMInject constructor(
    private val keyParser: KeyParser,
    private val cryptoStorageApi: CryptoStorageApi
) : LifecycleViewModel(), LogTagProvider {
    override val TAG: String = "UploaderViewModel"

    private val _state = MutableStateFlow<UploaderState>(UploaderState.Chooser)
    fun getState() = _state.asStateFlow()

    fun onShareLink(flipperKey: FlipperKey, context: Context) {
        val flipperName = flipperKey.path.nameWithoutExtension
        viewModelScope.launch {
            if (flipperKey.isBig()) {
                _state.emit(UploaderState.Prepare(isLongKey = true))
                val prepareLink = cryptoStorageApi.upload(
                    data = flipperKey.keyContent.openStream(),
                    path = flipperKey.path.pathToKey
                )
                processStorageResult(
                    link = prepareLink,
                    context = context,
                    flipperName = flipperName
                )
            } else {
                _state.emit(UploaderState.Prepare(isLongKey = false))
                val link = keyParser.keyToUrl(flipperKey)
                ShareHelper.shareText(
                    context = context,
                    title = flipperName,
                    text = link
                )
            }
        }
    }

    private suspend fun processStorageResult(
        link: Result<String>,
        context: Context,
        flipperName: String
    ) {
        link.onSuccess {
            ShareHelper.shareText(
                context = context,
                title = flipperName,
                text = it
            )
        }
        link.onFailure { exception ->
            error(exception) { "Error on upload file" }
            val error = when (exception) {
                is UnknownHostException -> ShareContentError.NO_INTERNET
                else -> ShareContentError.OTHER
            }
            _state.emit(UploaderState.Error(typeError = error))
        }
    }

    fun onShareFile(flipperKey: FlipperKey, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            ShareHelper.shareRawFile(
                context = context,
                data = flipperKey.mainFile.content.openStream(),
                resId = R.string.share_file,
                name = flipperKey.path.nameWithExtension
            )
        }
    }

    fun onRetry() {
        viewModelScope.launch {
            _state.emit(UploaderState.Chooser)
        }
    }
}
