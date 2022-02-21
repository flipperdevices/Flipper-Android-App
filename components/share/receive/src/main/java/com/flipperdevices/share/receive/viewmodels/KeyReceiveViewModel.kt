package com.flipperdevices.share.receive.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyReceiveViewModel(
    initialDeeplink: Deeplink?
) : ViewModel(), LogTagProvider {
    override val TAG = "KeyReceiveViewModel"
    private val state = MutableStateFlow(
        if (initialDeeplink == null) {
            ReceiveState.Finished
        } else ReceiveState.Pending(initialDeeplink)
    )

    @Inject
    lateinit var keyApi: KeyApi

    init {
        ComponentHolder.component<KeyReceiveComponent>().inject(this)
    }

    fun getState(): StateFlow<ReceiveState> = state

    fun save() {
        val localState = state.value
        if (localState !is ReceiveState.Pending) {
            info { "You can save key only from pending state, now is $state" }
            return
        }
        state.update { ReceiveState.Saving(localState.deeplink) }
        viewModelScope.launch {
            val path = localState.deeplink.path ?: return@launch
            val deeplinkContent = localState.deeplink.content as? DeeplinkContent.FFFContent
                ?: return@launch
            val flipperKey = FlipperKey(path, deeplinkContent.content)
            try {
                keyApi.insertKey(flipperKey)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                error(e) { "While save key $flipperKey" }
            }
            state.emit(ReceiveState.Finished)
        }
    }
}
