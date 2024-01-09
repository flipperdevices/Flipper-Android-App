package com.flipperdevices.notification.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.notification.api.FlipperAppNotificationApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider

class NotificationDialogViewModel @Inject constructor(
    settingsProvider: Provider<DataStore<Settings>>,
    flipperNotificationApiProvider: Provider<FlipperAppNotificationApi>,
    coroutineScopeProvider: Provider<CoroutineScope>
) : ViewModel() {
    private val settings by settingsProvider
    private val flipperNotificationApi by flipperNotificationApiProvider
    private val coroutineScope by coroutineScopeProvider
    fun isNotificationShown(): StateFlow<Boolean> = settings.data.map {
        it.notificationDialogShown.not()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onEnableNotification() {
        flipperNotificationApi.setSubscribeToUpdateAsync(
            isSubscribe = true,
            scope = coroutineScope,
            withNotificationSuccess = true
        )
        onDismiss()
    }

    fun onDismiss() {
        runBlocking {
            settings.updateData {
                it.toBuilder()
                    .setNotificationDialogShown(true)
                    .build()
            }
        }
    }
}
