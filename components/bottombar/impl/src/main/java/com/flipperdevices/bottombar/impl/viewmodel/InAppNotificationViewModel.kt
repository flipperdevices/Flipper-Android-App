package com.flipperdevices.bottombar.impl.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.inappnotification.api.InAppNotificationListener
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import javax.inject.Provider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import tangle.viewmodel.VMInject

class InAppNotificationViewModel @VMInject constructor(
    notificationStorageProvider: Provider<InAppNotificationStorage>
) : ViewModel(), InAppNotificationListener {
    private val notificationStorage by notificationStorageProvider

    private val notificationState = MutableStateFlow<InAppNotificationState>(
        InAppNotificationState.NotPresent
    )

    fun state(): StateFlow<InAppNotificationState> = notificationState

    fun onResume() {
        notificationStorage.subscribe(this)
    }

    fun onPause() {
        notificationStorage.unsubscribe()
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
