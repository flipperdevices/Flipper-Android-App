package com.flipperdevices.notification.api

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.notification.impl.R
import com.flipperdevices.notification.model.ChannelBlockedException
import com.flipperdevices.notification.model.NotificationPermissionState
import com.flipperdevices.notification.model.UpdateNotificationState
import com.flipperdevices.notification.model.UpdateNotificationStateInternal
import com.flipperdevices.notification.utils.NotificationPermissionHelper
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

private const val COUNT_PERMISSION_DENIED = 3
private const val TOPIC_UPDATE_FIRMWARE = "flipper_update_firmware_release"
private const val UPDATE_TOPIC_NOTIFICATION_CHANNEL = "flipper_update_firmware_channel"

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

    override fun isSubscribedToUpdateNotificationTopic(scope: CoroutineScope): Flow<UpdateNotificationState> {
        return combine(
            settingsDataStore.data,
            updateNotificationStateInternalFlow
        ) { settings, updateNotificationStateInternal ->
            when (updateNotificationStateInternal) {
                UpdateNotificationStateInternal.IN_PROGRESS -> UpdateNotificationState.IN_PROGRESS
                UpdateNotificationStateInternal.READY -> if (
                    settings.notification_topic_update_enabled
                ) {
                    when (permissionHelper.isPermissionGranted(UPDATE_TOPIC_NOTIFICATION_CHANNEL)) {
                        NotificationPermissionState.GRANTED -> UpdateNotificationState.ENABLED
                        NotificationPermissionState.DISABLED,
                        NotificationPermissionState.DISABLED_CHANNEL -> UpdateNotificationState.DISABLED
                    }
                } else {
                    UpdateNotificationState.DISABLED
                }
            }
        }
    }

    override fun setSubscribeToUpdateAsync(
        isSubscribe: Boolean,
        scope: CoroutineScope,
        withNotificationSuccess: Boolean
    ) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            setSubscribeToUpdate(isSubscribe, onRetry = {
                setSubscribeToUpdateAsync(isSubscribe, scope)
            }, withNotificationSuccess)
        }
    }

    @Suppress("LongMethod")
    private suspend fun setSubscribeToUpdate(
        isSubscribe: Boolean,
        onRetry: () -> Unit,
        withNotificationSuccess: Boolean
    ) = withLock(mutex, "set_update") {
        try {
            createNotificationChannel()
            setSubscribeToUpdateInternal(isSubscribe)
            if (withNotificationSuccess) {
                inAppNotification.addNotification(
                    InAppNotification.Successful(
                        titleId = R.string.notification_sucs_title,
                        descId = R.string.notification_sucs_desc
                    )
                )
            }
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
                        action = { openNotificationSettings(withChannel = false) }
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
        } catch (channelBlocked: ChannelBlockedException) {
            error(channelBlocked) { "Failed grant permission" }
            inAppNotification.addNotification(
                InAppNotification.Error(
                    titleId = R.string.notification_error_permission_title,
                    descId = R.string.notification_error_permission_desc,
                    actionTextId = R.string.notification_error_action_go_to_settings,
                    action = { openNotificationSettings(withChannel = true) }
                )
            )
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

        val task = if (isSubscribe) {
            Firebase.messaging.subscribeToTopic(TOPIC_UPDATE_FIRMWARE)
        } else {
            Firebase.messaging.unsubscribeFromTopic(TOPIC_UPDATE_FIRMWARE)
        }
        task.await()

        if (isSubscribe) {
            when (permissionHelper.isPermissionGranted(UPDATE_TOPIC_NOTIFICATION_CHANNEL)) {
                NotificationPermissionState.GRANTED -> {}
                NotificationPermissionState.DISABLED -> if (!permissionHelper.requestPermission()) {
                    throw SecurityException()
                }

                NotificationPermissionState.DISABLED_CHANNEL -> throw ChannelBlockedException()
            }
        }

        settingsDataStore.updateData {
            it.copy(
                notification_topic_update_enabled = isSubscribe
            )
        }
    }

    private fun openNotificationSettings(withChannel: Boolean) {
        var settingsIntent: Intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        if (withChannel) {
            settingsIntent = settingsIntent.putExtra(
                Settings.EXTRA_CHANNEL_ID,
                UPDATE_TOPIC_NOTIFICATION_CHANNEL
            )
        }
        context.startActivity(settingsIntent)
    }

    private fun createNotificationChannel() {
        val name = context.getString(R.string.notification_channel_title)
        val descriptionText = context.getString(R.string.notification_channel_desc)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(UPDATE_TOPIC_NOTIFICATION_CHANNEL, name, importance)
        mChannel.description = descriptionText
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.createNotificationChannel(mChannel)
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
