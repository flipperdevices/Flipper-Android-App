package com.flipperdevices.share.receive.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.keyedit.api.KeyEditApi
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.keyedit.api.toNotSavedFlipperFile
import com.flipperdevices.share.api.ShareContentError
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.di.KeyReceiveComponent
import com.flipperdevices.share.receive.model.FlipperKeyParseException
import com.flipperdevices.share.receive.model.ReceiveState
import com.github.terrakok.cicerone.Router
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val NOTIFICATION_DURATION_MS = 3 * 1000L

class KeyReceiveViewModel(
    initialDeeplink: Deeplink?,
    application: Application
) : AndroidViewModel(application), LogTagProvider {
    override val TAG = "KeyReceiveViewModel"
    private val internalDeeplinkFlow = MutableStateFlow(initialDeeplink)
    private val state = MutableStateFlow<ReceiveState>(ReceiveState.NotStarted)

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var keyEditApi: KeyEditApi

    @Inject
    lateinit var utilsKeyApi: UtilsKeyApi

    @Inject
    lateinit var keyParser: KeyParser

    @Inject
    lateinit var notificationStorage: InAppNotificationStorage

    @Inject
    lateinit var flipperKeyParserHelper: FlipperKeyParserHelper

    init {
        ComponentHolder.component<KeyReceiveComponent>().inject(this)
        internalDeeplinkFlow.onEach {
            parseFlipperKey()
        }.launchIn(viewModelScope)
    }

    private suspend fun parseFlipperKey() {
        internalDeeplinkFlow.onEach {
            val flipperKey = flipperKeyParserHelper.toFlipperKey(it)
            flipperKey.onSuccess { localFlipperKey ->
                val newPath = utilsKeyApi.findAvailablePath(localFlipperKey.getKeyPath())
                val newFlipperKey = localFlipperKey.copy(
                    mainFile = localFlipperKey.mainFile.copy(
                        path = newPath.path
                    ),
                    deleted = newPath.deleted
                )
                state.emit(ReceiveState.Pending(newFlipperKey, keyParser.parseKey(newFlipperKey)))
            }
            flipperKey.onFailure { exception ->
                error(exception) { "Error on parse flipperKey" }
                when (exception) {
                    is FlipperKeyParseException -> state.emit(ReceiveState.Finished)
                    is UnknownHostException -> state.emit(
                        ReceiveState.Error(ShareContentError.NO_INTERNET)
                    )
                    is UnknownServiceException -> state.emit(
                        ReceiveState.Error(ShareContentError.SERVER_ERROR)
                    )
                    else -> state.emit(ReceiveState.Error(ShareContentError.OTHER))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getState() = state.asStateFlow()

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
        val isStateSaved = state.compareAndSet(
            localState,
            ReceiveState.Saving(localState.flipperKey, localState.parsed)
        )
        if (!isStateSaved) {
            onSave()
            return
        }

        viewModelScope.launch {
            try {
                simpleKeyApi.insertKey(localState.flipperKey)
            } catch (e: Exception) {
                error(e) { "While save key ${localState.flipperKey}" }
                getApplication<Application>().toast(R.string.receive_error_conflict)
                state.emit(ReceiveState.Pending(localState.flipperKey, localState.parsed))
                return@launch
            }

            notificationStorage.addNotification(
                InAppNotification(
                    title = localState.flipperKey.path.nameWithoutExtension,
                    descriptionId = R.string.receive_notification_description,
                    durationMs = NOTIFICATION_DURATION_MS
                )
            )
            state.emit(ReceiveState.Finished)
        }
    }

    fun onEdit(router: Router) {
        viewModelScope.launch {
            when (val localState = state.value) {
                is ReceiveState.Pending -> {
                    val flipperKey = localState.flipperKey
                    val notSavedKey = NotSavedFlipperKey(
                        mainFile = flipperKey.mainFile.toNotSavedFlipperFile(getApplication()),
                        additionalFiles = listOf(),
                        notes = flipperKey.notes
                    )
                    val title = flipperKey.mainFile.path.nameWithoutExtension
                    router.navigateTo(keyEditApi.getScreen(notSavedKey, title))
                }
                else -> {}
            }
        }
    }
}
