package com.flipperdevices.notification.noop

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.notification.api.FlipperAppNotificationApi
import com.flipperdevices.notification.model.UpdateNotificationState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperAppNotificationApi::class)
class FlipperAppNotificationApiNoopImpl @Inject constructor(
    private val inAppNotificationStorage: InAppNotificationStorage
) : FlipperAppNotificationApi {
    override fun init() = Unit

    override fun isSubscribedToUpdateNotificationTopic(scope: CoroutineScope): StateFlow<UpdateNotificationState> {
        return MutableStateFlow(UpdateNotificationState.DISABLED)
    }

    override fun setSubscribeToUpdateAsync(
        isSubscribe: Boolean,
        scope: CoroutineScope,
        withNotificationSuccess: Boolean
    ) {
        inAppNotificationStorage.addNotification(
            InAppNotification.Error(
                titleId = R.string.notification_disabled_title,
                descId = R.string.notification_disabled_desc,
                actionTextId = null,
                action = null
            )
        )
    }
}
