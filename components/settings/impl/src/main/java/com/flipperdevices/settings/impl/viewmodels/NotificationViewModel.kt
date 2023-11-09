package com.flipperdevices.settings.impl.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.notification.api.FlipperAppNotificationApi
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class NotificationViewModel @VMInject constructor(
    private val notificationApi: FlipperAppNotificationApi
) : ViewModel(), LogTagProvider {
    override val TAG = "NotificationViewModel"

    fun getNotificationToggleState() = notificationApi
        .isSubscribedToUpdateNotificationTopic(viewModelScope)

    fun switchToggle(newState: Boolean) {
        notificationApi.setSubscribeToUpdateAsync(newState, viewModelScope)
    }
}
