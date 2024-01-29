package com.flipperdevices.settings.impl.viewmodels

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.notification.api.FlipperAppNotificationApi
import com.flipperdevices.notification.model.UpdateNotificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class NotificationViewModel @Inject constructor(
    private val notificationApi: FlipperAppNotificationApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "NotificationViewModel"
    private val notificationStateFlow = MutableStateFlow(UpdateNotificationState.IN_PROGRESS)

    init {
        notificationApi
            .isSubscribedToUpdateNotificationTopic(viewModelScope)
            .onEach {
                notificationStateFlow.emit(it)
            }.launchIn(viewModelScope)
    }

    fun getNotificationToggleState() = notificationStateFlow

    fun switchToggle(newState: Boolean) {
        notificationApi.setSubscribeToUpdateAsync(newState, viewModelScope)
    }
}
