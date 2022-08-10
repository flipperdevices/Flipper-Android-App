package com.flipperdevices.share.receive.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.di.KeyReceiveComponent
import com.flipperdevices.share.receive.model.ReceiveState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    lateinit var utilsKeyApi: UtilsKeyApi

    @Inject
    lateinit var keyParser: KeyParser

    @Inject
    lateinit var notificationStorage: InAppNotificationStorage

    init {
        ComponentHolder.component<KeyReceiveComponent>().inject(this)
        internalDeeplinkFlow.onEach {
            var flipperKey = FlipperKeyParserHelper.toFlipperKey(it)
            if (flipperKey == null) {
                state.emit(ReceiveState.Finished)
                return@onEach
            }
            val newPath = utilsKeyApi.findAvailablePath(flipperKey.path)
            flipperKey = flipperKey.copy(path = newPath)
            state.emit(ReceiveState.Pending(flipperKey, keyParser.parseKey(flipperKey)))
        }.launchIn(viewModelScope)
    }

    fun getState(): StateFlow<ReceiveState> = state

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

    fun onEdit() {
        val localState = state.value
        if (localState !is ReceiveState.Pending) {
            info { "You can edit key only from pending state, now is $state" }
            return
        }
        val isStateSaved = state.compareAndSet(
            localState,
            ReceiveState.Editing(
                flipperKey = localState.flipperKey,
                parsed = localState.parsed
            )
        )
        if (!isStateSaved) {
            onEdit()
            return
        }
    }

    fun onCloseEdit(flipperKey: FlipperKey) {
        val localState = state.value
        if (localState !is ReceiveState.Editing) {
            info { "You can return from edit state only on edit state, now is $state" }
            return
        }
        val isStateSaved = state.compareAndSet(
            localState,
            ReceiveState.NotStarted
        )
        if (!isStateSaved) {
            onCloseEdit(flipperKey)
            return
        }
        viewModelScope.launch {
            val parsed = keyParser.parseKey(flipperKey)

            state.emit(ReceiveState.Pending(flipperKey, parsed))
        }
    }

    fun onBack(): Boolean {
        val currentState = state.value
        if (currentState !is ReceiveState.Editing) {
            return false
        }
        val isStateSaved = state.compareAndSet(
            currentState,
            ReceiveState.Pending(
                currentState.flipperKey,
                currentState.parsed
            )
        )
        if (!isStateSaved) {
            return onBack()
        }
        return true
    }
}
