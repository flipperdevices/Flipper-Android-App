package com.flipperdevices.settings.impl.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.notification.api.FlipperAppNotificationApi
import com.flipperdevices.settings.impl.R
import java.io.IOException
import java.net.UnknownHostException
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class NotificationViewModel @VMInject constructor(
    private val inAppNotification: InAppNotificationStorage,
    private val notificationApi: FlipperAppNotificationApi
) : ViewModel(), LogTagProvider {
    override val TAG = "NotificationViewModel"

    fun getNotificationToggleState() = notificationApi
        .isSubscribedToUpdateNotificationTopic(viewModelScope)

    fun switchToggle(newState: Boolean) {
        viewModelScope.launch {
            try {
                notificationApi.setSubscribeToUpdate(newState)
            } catch (uhe: UnknownHostException) {
                error(uhe) { "Failed subscribe to topic" }
                inAppNotification.addNotification(
                    InAppNotification.Error(
                        titleId = R.string.app_notification_error_internet_title,
                        descId = R.string.app_notification_error_internet_desc
                    )
                )
            } catch (ioException: IOException) {
                error(ioException) { "Failed subscribe to topic" }
                inAppNotification.addNotification(
                    InAppNotification.Error(
                        titleId = R.string.app_notification_error_server_title,
                        descId = R.string.app_notification_error_server_desc
                    )
                )
            } catch (generalError: Throwable) {
                error(generalError) { "Failed subscribe to topic" }
                inAppNotification.addNotification(
                    InAppNotification.Error(
                        titleId = R.string.app_notification_error_general_title,
                        descId = R.string.app_notification_error_general_desc
                    )
                )
            }
        }
    }
}