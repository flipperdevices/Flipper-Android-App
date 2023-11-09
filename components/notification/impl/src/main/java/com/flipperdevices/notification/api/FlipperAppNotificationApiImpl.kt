package com.flipperdevices.notification.api

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.notification.impl.R
import com.flipperdevices.notification.model.UpdateNotificationState
import com.flipperdevices.notification.model.UpdateNotificationStateInternal
import com.flipperdevices.notification.utils.NotificationPermissionHelper
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.squareup.anvil.annotations.ContributesBinding
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private const val COUNT_PERMISSION_DENIED = 3
private const val TOPIC_UPDATE_FIRMWARE = "flipper_update_firmware_release"

@Singleton
@ContributesBinding(AppGraph::class, FlipperAppNotificationApi::class)
class FlipperAppNotificationApiImpl @Inject constructor(
    dataStoreProvider: Provider<DataStore<com.flipperdevices.core.preference.pb.Settings>>,
    permissionHelperProvider: Provider<NotificationPermissionHelper>,
    inAppNotificationStorageProvider: Provider<InAppNotificationStorage>,
    private val context: Context
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
    private var permissionRequestDenied = 0

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

    override fun setSubscribeToUpdateAsync(
        isSubscribe: Boolean,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.Default) {
            setSubscribeToUpdate(isSubscribe, onRetry = {
                setSubscribeToUpdateAsync(isSubscribe, scope)
            })
        }
    }

    private suspend fun setSubscribeToUpdate(
        isSubscribe: Boolean,
        onRetry: () -> Unit
    ) = withLock(mutex, "set_update") {
        try {
            setSubscribeToUpdateInternal(isSubscribe)
        } catch (uhe: UnknownHostException) {
            error(uhe) { "Failed subscribe to topic" }
            inAppNotification.addNotification(
                InAppNotification.Error(
                    titleId = R.string.notification_error_internet_title,
                    descId = R.string.notification_error_internet_desc,
                    actionTextId = R.string.notification_error_action_retry,
                    action = onRetry
                )
            )
        } catch (ioException: IOException) {
            error(ioException) { "Failed subscribe to topic" }
            inAppNotification.addNotification(
                InAppNotification.Error(
                    titleId = R.string.notification_error_server_title,
                    descId = R.string.notification_error_server_desc,
                    actionTextId = R.string.notification_error_action_retry,
                    action = onRetry
                )
            )
        } catch (securityException: SecurityException) {
            error(securityException) { "Failed grant permission" }
            if (++permissionRequestDenied >= COUNT_PERMISSION_DENIED) {
                inAppNotification.addNotification(
                    InAppNotification.Error(
                        titleId = R.string.notification_error_permission_title,
                        descId = R.string.notification_error_permission_desc,
                        actionTextId = R.string.notification_error_action_go_to_settings,
                        action = this::openNotificationSettings
                    )
                )
            } else {
                inAppNotification.addNotification(
                    InAppNotification.Error(
                        titleId = R.string.notification_error_permission_title,
                        descId = R.string.notification_error_permission_desc,
                        actionTextId = R.string.notification_error_action_retry,
                        action = onRetry
                    )
                )
            }
        } catch (generalError: Throwable) {
            error(generalError) { "Failed subscribe to topic" }
            inAppNotification.addNotification(
                InAppNotification.Error(
                    titleId = R.string.notification_error_general_title,
                    descId = R.string.notification_error_general_desc,
                    actionTextId = R.string.notification_error_action_retry,
                    action = onRetry
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
                throw SecurityException()
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

    private fun openNotificationSettings() {
        val settingsIntent: Intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        //TODO: .putExtra(Settings.EXTRA_CHANNEL_ID, MY_CHANNEL_ID)
        context.startActivity(settingsIntent)
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
