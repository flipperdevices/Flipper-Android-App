package com.flipperdevices.share.receive.viewmodels

import android.app.Application
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.helpers.FlipperKeyParserHelper
import com.flipperdevices.share.receive.helpers.ReceiveKeyActionHelper
import com.flipperdevices.share.receive.models.ReceiveState
import com.flipperdevices.share.receive.models.ReceiverError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.net.UnknownHostException
import java.net.UnknownServiceException

class KeyReceiveViewModel @AssistedInject constructor(
    @Assisted initialDeeplink: Deeplink.RootLevel.SaveKey,
    private val application: Application,
    private val synchronizationApi: SynchronizationApi,
    private val flipperKeyParserHelper: FlipperKeyParserHelper,
    private val receiveKeyActionHelper: ReceiveKeyActionHelper
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "KeyReceiveViewModel"
    private val internalDeeplinkFlow = MutableStateFlow(initialDeeplink)

    private val state = MutableStateFlow<ReceiveState>(ReceiveState.NotStarted)
    private var job: Job? = null
    fun getState() = state.asStateFlow()

    init {
        job = internalDeeplinkFlow.onEach {
            parseFlipperKey(it)
        }.launchIn(viewModelScope + Dispatchers.Default)
    }

    private suspend fun parseFlipperKey(deeplink: Deeplink.RootLevel.SaveKey) {
        val flipperKey = flipperKeyParserHelper.toFlipperKey(deeplink)
        flipperKey.onSuccess { localFlipperKey ->
            processSuccessfullyParseKey(localFlipperKey)
        }
        flipperKey.onFailure { exception ->
            processFailureParseKey(exception)
        }
    }

    private suspend fun processSuccessfullyParseKey(flipperKey: FlipperKey) {
        val newKey = receiveKeyActionHelper.findNewPathAndCloneKey(flipperKey)
        val keyParsed = receiveKeyActionHelper.parseKey(newKey)
        state.emit(ReceiveState.Pending(newKey, keyParsed))
    }

    private suspend fun processFailureParseKey(exception: Throwable) {
        error(exception) { "Error on parse flipperKey" }
        val errorType = when (exception) {
            is UnknownHostException -> ReceiverError.NO_INTERNET_CONNECTION
            is UnknownServiceException -> ReceiverError.CANT_CONNECT_TO_SERVER
            is ClientRequestException -> {
                val exceptionStatus = exception.response.status
                if (exceptionStatus == HttpStatusCode.NotFound) {
                    ReceiverError.EXPIRED_LINK
                } else {
                    ReceiverError.CANT_CONNECT_TO_SERVER
                }
            }

            else -> ReceiverError.INVALID_FILE_FORMAT
        }
        state.emit(ReceiveState.Error(errorType))
    }

    fun onRetry() {
        val oldJob = job
        job = viewModelScope.launch(Dispatchers.Default) {
            oldJob?.cancelAndJoin()
            internalDeeplinkFlow.collect {
                state.emit(ReceiveState.NotStarted)
                parseFlipperKey(it)
            }
        }
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
            val saveKeyResult = receiveKeyActionHelper.saveKey(newState.flipperKey)

            saveKeyResult.onFailure { exception ->
                error(exception) { "While save key ${localState.flipperKey}" }
                application.toast(R.string.receive_error_conflict)
                state.emit(ReceiveState.Pending(localState.flipperKey, localState.parsed))
                return@launch
            }

            saveKeyResult.onSuccess {
                state.emit(ReceiveState.Finished)
            }
        }
    }

    fun onFinish() {
        synchronizationApi.startSynchronization(force = true)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            initialDeeplink: Deeplink.RootLevel.SaveKey
        ): KeyReceiveViewModel
    }
}
