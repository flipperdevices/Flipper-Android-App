package com.flipperdevices.notification.api

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.notification.impl.R
import com.flipperdevices.notification.model.UpdateNotificationState
import com.flipperdevices.notification.model.UpdateNotificationStateInternal
import com.flipperdevices.notification.utils.NotificationPermissionHelper
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.squareup.anvil.annotations.ContributesBinding
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
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

private const val TOPIC_UPDATE_FIRMWARE = "flipper_update_firmware_release"

@Singleton
@ContributesBinding(AppGraph::class, FlipperAppNotificationApi::class)
class FlipperAppNotificationApiImpl @Inject constructor(
    dataStoreProvider: Provider<DataStore<Settings>>,
    permissionHelperProvider: Provider<NotificationPermissionHelper>,
    inAppNotificationStorageProvider: Provider<InAppNotificationStorage>
) : FlipperAppNotificationApi,
    LogTagProvider {
    override val TAG = "FlipperAppNotificationApi"

    private val settingsDataStore by dataStoreProvider
    private val permissionHelper by permissionHelperProvider
    private val inAppNotification by inAppNotificationStorageProvider
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
                UpdateNotificationStateInternal.READY -> if (
                    settings.notificationTopicUpdateEnabled &&
                    permissionHelper.isPermissionGranted()
                ) {
                    UpdateNotificationState.ENABLED
                } else {
                    UpdateNotificationState.DISABLED
                }
            }
        }.stateIn(scope, SharingStarted.WhileSubscribed(), UpdateNotificationState.IN_PROGRESS)
    }

    override suspend fun setSubscribeToUpdate(
        isSubscribe: Boolean
    ) = withLock(mutex, "set_update") {
        try {
            setSubscribeToUpdateInternal(isSubscribe)
        } catch (uhe: UnknownHostException) {
            error(uhe) { "Failed subscribe to topic" }
            inAppNotification.addNotification(
                InAppNotification.Error(
                    titleId = R.string.notification_error_internet_title,
                    descId = R.string.notification_error_internet_desc
                )
            )
        } catch (ioException: IOException) {
            error(ioException) { "Failed subscribe to topic" }
            inAppNotification.addNotification(
                InAppNotification.Error(
                    titleId = R.string.notification_error_server_title,
                    descId = R.string.notification_error_server_desc
                )
            )
        } catch (generalError: Throwable) {
            error(generalError) { "Failed subscribe to topic" }
            inAppNotification.addNotification(
                InAppNotification.Error(
                    titleId = R.string.notification_error_general_title,
                    descId = R.string.notification_error_general_desc
                )
            )
        } finally {
            withContext(NonCancellable) {
                updateNotificationStateInternalFlow.emit(UpdateNotificationStateInternal.READY)
            }
        }
    }

    private suspend fun setSubscribeToUpdateInternal(isSubscribe: Boolean) {
        updateNotificationStateInternalFlow.emit(UpdateNotificationStateInternal.IN_PROGRESS)

        if (isSubscribe && !permissionHelper.isPermissionGranted()) {
            val permissionRequestResult = permissionHelper.requestPermission()
            if (!permissionRequestResult) {
                error { "Failed grant permission" }
                /*inAppNotification.addNotification(
                    InAppNotification
                )*/
                return
            }
        }

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
