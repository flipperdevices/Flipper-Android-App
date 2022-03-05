package com.flipperdevices.inappnotification.impl.storage

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.inappnotification.api.InAppNotificationListener
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.squareup.anvil.annotations.ContributesBinding
import java.util.Stack
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TIMER_DELAY = 1.seconds

@Singleton
@ContributesBinding(AppGraph::class, InAppNotificationStorage::class)
class InAppNotificationStorageImpl @Inject constructor() :
    InAppNotificationStorage,
    LogTagProvider {
    override val TAG = "InAppNotificationStorage"
    private val storageContext = Dispatchers.Default.limitedParallelism(1)
    private val coroutineScope = CoroutineScope(storageContext + SupervisorJob())
    private val timerTask = TimerTask(
        delayDuration = TIMER_DELAY,
        coroutineScope = coroutineScope,
        block = { invalidate() }
    )

    private val pendingNotification = Stack<InAppNotification>()
    private var listener: InAppNotificationListener? = null
    private var nextNotificationTime = 0L

    override fun subscribe(listener: InAppNotificationListener) {
        if (this.listener != null) {
            error("For now this storage support only one listener in one time")
        }
        this.listener = listener
        timerTask.start()
        coroutineScope.launch {
            invalidate()
        }
    }

    override fun unsubscribe() {
        this.listener = null
        timerTask.shutdown()
    }

    override suspend fun addNotification(notification: InAppNotification) =
        withContext(storageContext) {
            pendingNotification.push(notification)
            invalidate()
        }

    private suspend fun invalidate() = withContext(storageContext) {
        val notificationListener = listener ?: return@withContext

        val currentTime = System.currentTimeMillis()
        if (currentTime < nextNotificationTime) {
            return@withContext
        }

        if (pendingNotification.empty()) {
            return@withContext
        }

        val notificationToShown = pendingNotification.pop()
        notificationListener.onNewNotification(notificationToShown)
        nextNotificationTime = System.currentTimeMillis() + notificationToShown.durationMs
    }
}
