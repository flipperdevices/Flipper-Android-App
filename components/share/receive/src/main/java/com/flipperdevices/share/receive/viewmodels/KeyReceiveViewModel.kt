package com.flipperdevices.share.receive.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.share.receive.di.KeyReceiveComponent
import com.flipperdevices.share.receive.model.ReceiveState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyReceiveViewModel(
    initialDeeplink: Deeplink?
) : ViewModel(), LogTagProvider {
    override val TAG = "KeyReceiveViewModel"
    private val internalDeeplinkFlow = MutableStateFlow(initialDeeplink)
    private val state = MutableStateFlow<ReceiveState>(ReceiveState.NotStarted)

    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var keyParser: KeyParser

    init {
        ComponentHolder.component<KeyReceiveComponent>().inject(this)
        internalDeeplinkFlow.onEach {
            val flipperKey = it?.toFlipperKey()
            if (flipperKey == null) {
                state.emit(ReceiveState.Finished)
                return@onEach
            }
            state.emit(ReceiveState.Pending(it, keyParser.parseKey(flipperKey)))
        }.launchIn(viewModelScope)
    }

    fun getState(): StateFlow<ReceiveState> = state

    fun save() {
        val localState = state.value
        if (localState !is ReceiveState.Pending) {
            info { "You can save key only from pending state, now is $state" }
            return
        }
        state.update { ReceiveState.Saving(localState.deeplink, localState.parsed) }
        viewModelScope.launch {
            val flipperKey = localState.deeplink.toFlipperKey() ?: return@launch
            try {
                keyApi.insertKey(flipperKey)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                error(e) { "While save key $flipperKey" }
            }
            state.emit(ReceiveState.Finished)
        }
    }
}

private fun Deeplink?.toFlipperKey(): FlipperKey? {
    val path = this?.path ?: return null
    val deeplinkContent = this.content as? DeeplinkContent.FFFContent
        ?: return null
    return FlipperKey(path, deeplinkContent.content)
}
