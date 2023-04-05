package com.flipperdevices.bottombar.impl.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.inappnotification.api.InAppNotificationListener
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import tangle.viewmodel.VMInject
import javax.inject.Provider

class InAppNotificationViewModel @VMInject constructor(
    notificationStorageProvider: Provider<InAppNotificationStorage>
) : ViewModel(), InAppNotificationListener, LogTagProvider {
    override val TAG = "InAppNotificationViewModel"

    private val notificationStorage by notificationStorageProvider

    private val notificationState = MutableStateFlow<InAppNotificationState>(
        InAppNotificationState.NotPresent
    )

    fun state(): StateFlow<InAppNotificationState> = notificationState

    fun onLifecycleEvent(event: Lifecycle.Event) {
        verbose { "#onLifecycleEvent $event" }
        when (event) {
            Lifecycle.Event.ON_RESUME -> notificationStorage.subscribe(this)
            Lifecycle.Event.ON_PAUSE -> notificationStorage.unsubscribe()
            else -> {}
        }
    }

    fun onNotificationHidden(notification: InAppNotification) {
        notificationState.update {
            if (it is InAppNotificationState.ShownNotification &&
                it.notification == notification
            ) {
                InAppNotificationState.NotPresent
            } else {
                it
            }
        }
    }

    override suspend fun onNewNotification(notification: InAppNotification) {
        notificationState.emit(InAppNotificationState.ShownNotification(notification))
    }
}

sealed class InAppNotificationState {
    object NotPresent : InAppNotificationState()
    data class ShownNotification(
        val notification: InAppNotification
    ) : InAppNotificationState()
}
