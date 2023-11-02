package com.flipperdevices.notification.api

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.withLock
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TOPIC_UPDATE_FIRMWARE = "flipper_update_firmware_release"

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
    private val mutex = Mutex()

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

    override suspend fun setSubscribeToUpdate(isSubscribe: Boolean) =
        withLock(mutex, "set_update") {
            try {
                updateNotificationStateInternalFlow.emit(UpdateNotificationStateInternal.IN_PROGRESS)
                val task = if (isSubscribe) {
                    Firebase.messaging.subscribeToTopic(TOPIC_UPDATE_FIRMWARE)
                } else {
                    Firebase.messaging.unsubscribeFromTopic(TOPIC_UPDATE_FIRMWARE)
                }
                task.await()
                settingsDataStore.updateData {
                    it.toBuilder()
                        .setNotificationTopicUpdateEnabled(isSubscribe)
                        .build()
                }
            } finally {
                withContext(NonCancellable) {
                    updateNotificationStateInternalFlow.emit(UpdateNotificationStateInternal.READY)
                }
            }
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
