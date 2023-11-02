package com.flipperdevices.notification.api

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.notification.model.UpdateNotificationState
import com.flipperdevices.notification.model.UpdateNotificationStateInternal
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Singleton
@ContributesBinding(AppGraph::class, FlipperAppNotificationApi::class)
class FlipperAppNotificationApiImpl @Inject constructor(
    private val dataStoreProvider: Provider<DataStore<Settings>>
) : FlipperAppNotificationApi,
    LogTagProvider {
    override val TAG = "FlipperAppNotificationApi"

    private val settingsDataStore by dataStoreProvider
    private val updateNotificationStateInternalFlow = MutableStateFlow(
        UpdateNotificationStateInternal.READY
    )

    override fun isSubscribedToUpdateNotificationTopic(scope: CoroutineScope): StateFlow<UpdateNotificationState> {
        return combine(
            settingsDataStore.data,
            updateNotificationStateInternalFlow
        ) { settings, updateNotificationStateInternal ->
            when (updateNotificationStateInternal) {
                UpdateNotificationStateInternal.IN_PROGRESS -> UpdateNotificationState.IN_PROGRESS
                UpdateNotificationStateInternal.READY -> if (settings.notificationTopicUpdateEnabled) {
                    UpdateNotificationState.ENABLED
                } else {
                    UpdateNotificationState.DISABLED
                }
            }
        }.stateIn(scope, SharingStarted.WhileSubscribed(), UpdateNotificationState.IN_PROGRESS)
    }

    override fun init() {
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                error(task.exception) { "Can't init FCM registration token" }
                return@addOnCompleteListener
            }
            val token = task.result

            info { "Init FCM success with token $token " }
        }
    }
}
