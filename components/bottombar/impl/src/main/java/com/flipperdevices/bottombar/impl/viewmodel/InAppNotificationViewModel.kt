package com.flipperdevices.bottombar.impl.viewmodel

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.inappnotification.api.InAppNotificationListener
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Provider

class InAppNotificationViewModel @AssistedInject constructor(
    @Assisted private val lifecycleOwner: LifecycleOwner,
    notificationStorageProvider: Provider<InAppNotificationStorage>
) : DecomposeViewModel(), InAppNotificationListener, LogTagProvider {
    override val TAG = "InAppNotificationViewModel"

    private val notificationStorage by notificationStorageProvider

    private val notificationState = MutableStateFlow<InAppNotificationState>(
        InAppNotificationState.NotPresent
    )

    private val lifecycleCallback = object : Lifecycle.Callbacks {
        override fun onResume() = notificationStorage.subscribe(this@InAppNotificationViewModel)
        override fun onPause() = notificationStorage.unsubscribe()
    }

    init {
        lifecycleOwner.lifecycle.subscribe(lifecycleCallback)
    }

    fun state(): StateFlow<InAppNotificationState> = notificationState

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

    override fun onDestroy() {
        super.onDestroy()
        lifecycleOwner.lifecycle.unsubscribe(lifecycleCallback)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            lifecycleOwner: LifecycleOwner
        ): InAppNotificationViewModel
    }
}

sealed class InAppNotificationState {
    data object NotPresent : InAppNotificationState()
    data class ShownNotification(
        val notification: InAppNotification
    ) : InAppNotificationState()
}
