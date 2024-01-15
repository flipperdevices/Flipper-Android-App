package com.flipperdevices.settings.impl.viewmodels

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.notification.api.FlipperAppNotificationApi
import javax.inject.Inject

class NotificationViewModel @Inject constructor(
    private val notificationApi: FlipperAppNotificationApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "NotificationViewModel"
    private val notificationStateFlow = notificationApi
        .isSubscribedToUpdateNotificationTopic(viewModelScope)

    fun getNotificationToggleState() = notificationStateFlow

    fun switchToggle(newState: Boolean) {
        notificationApi.setSubscribeToUpdateAsync(newState, viewModelScope)
    }
}
