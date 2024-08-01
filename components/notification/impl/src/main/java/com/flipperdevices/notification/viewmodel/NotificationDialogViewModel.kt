package com.flipperdevices.notification.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.notification.api.FlipperAppNotificationApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider

class NotificationDialogViewModel @Inject constructor(
    settingsProvider: Provider<DataStore<Settings>>,
    flipperNotificationApiProvider: Provider<FlipperAppNotificationApi>,
    coroutineScopeProvider: Provider<CoroutineScope>
) : DecomposeViewModel() {
    private val settings by settingsProvider
    private val flipperNotificationApi by flipperNotificationApiProvider
    private val coroutineScope by coroutineScopeProvider
    private val isNotificationShownStateFlow = MutableStateFlow(false)

    init {
        settings.data.onEach {
            isNotificationShownStateFlow.emit(it.notification_dialog_shown.not())
        }.launchIn(viewModelScope)
    }

    fun isNotificationShown(): StateFlow<Boolean> = isNotificationShownStateFlow.asStateFlow()

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
                it.copy(
                    notification_dialog_shown = true
                )
            }
        }
    }
}
