package com.flipperdevices.share.receive.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.fragments.EXTRA_KEY_DEEPLINK
import com.flipperdevices.share.receive.helpers.FlipperKeyParserHelper
import com.flipperdevices.share.receive.helpers.ReceiveKeyActionHelper
import com.flipperdevices.share.receive.models.ReceiveState
import com.flipperdevices.share.receive.models.ReceiverError
import com.github.terrakok.cicerone.Router
import java.io.FileNotFoundException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class KeyReceiveViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_DEEPLINK)
    initialDeeplink: Deeplink?,
    application: Application,
    private val synchronizationApi: SynchronizationApi,
    private val flipperKeyParserHelper: FlipperKeyParserHelper,
    private val receiveKeyActionHelper: ReceiveKeyActionHelper
) : AndroidLifecycleViewModel(application), LogTagProvider {
    override val TAG = "KeyReceiveViewModel"
    private val internalDeeplinkFlow = MutableStateFlow(initialDeeplink)

    private val state = MutableStateFlow<ReceiveState>(ReceiveState.NotStarted)
    fun getState() = state.asStateFlow()

    init {
        internalDeeplinkFlow.onEach {
            parseFlipperKey()
        }.launchIn(viewModelScope)
    }

    private suspend fun parseFlipperKey() {
        internalDeeplinkFlow.onEach {
            val flipperKey = flipperKeyParserHelper.toFlipperKey(it)
            flipperKey.onSuccess { localFlipperKey ->
                processSuccessfullyParseKey(localFlipperKey)
            }
            flipperKey.onFailure { exception ->
                processFailureParseKey(exception)
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun processSuccessfullyParseKey(flipperKey: FlipperKey) {
        val newKey = receiveKeyActionHelper.findNewPathAndCloneKey(flipperKey)
        val keyParsed = receiveKeyActionHelper.parseKey(newKey)
        state.emit(ReceiveState.Pending(newKey, keyParsed))
    }

    private suspend fun processFailureParseKey(exception: Throwable) {
        error(exception) { "Error on parse flipperKey" }
        when (exception) {
            is UnknownHostException -> state.emit(
                ReceiveState.Error(ReceiverError.NO_INTERNET_CONNECTION)
            )
            is UnknownServiceException -> state.emit(
                ReceiveState.Error(ReceiverError.CANT_CONNECT_TO_SERVER)
            )
            is FileNotFoundException -> state.emit(
                ReceiveState.Error(ReceiverError.EXPIRED_LINK)
            )
            else -> ReceiveState.Error(ReceiverError.INVALID_FILE_FORMAT)
        }
    }

    fun onRetry() {
        internalDeeplinkFlow.onEach {
            state.emit(ReceiveState.NotStarted)
            parseFlipperKey()
        }.launchIn(viewModelScope)
    }

    fun onSave() {
        val localState = state.value
        if (localState !is ReceiveState.Pending) {
            info { "You can save key only from pending state, now is $state" }
            return
        }
        val newState = localState.copy(isSaving = true)

        val isStateSaved = state.compareAndSet(localState, newState)
        if (!isStateSaved) {
            onSave()
            return
        }

        viewModelScope.launch {
            val saveResult = receiveKeyActionHelper.saveKey(newState.flipperKey)

            saveResult.onFailure { exception ->
                error(exception) { "While save key ${localState.flipperKey}" }
                getApplication<Application>().toast(R.string.receive_error_conflict)
                state.emit(ReceiveState.Pending(localState.flipperKey, localState.parsed))
                return@launch
            }

            saveResult.onSuccess {
                state.emit(ReceiveState.Finished)
            }
        }
    }

    fun onEdit(router: Router) {
        viewModelScope.launch {
            when (val localState = state.value) {
                is ReceiveState.Pending -> {
                    receiveKeyActionHelper.editKey(
                        flipperKey = localState.flipperKey,
                        router = router,
                        context = getApplication()
                    )
                }
                else -> {}
            }
        }
    }

    fun onFinish(router: Router) {
        synchronizationApi.startSynchronization(force = true)
        router.exit()
    }
}
