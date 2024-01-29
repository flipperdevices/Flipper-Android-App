package com.flipperdevices.inappnotification.impl.storage

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.inappnotification.api.InAppNotificationListener
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.Stack
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

private val TIMER_DELAY = 1.seconds

@Singleton
@ContributesBinding(AppGraph::class, InAppNotificationStorage::class)
class InAppNotificationStorageImpl @Inject constructor() :
    InAppNotificationStorage,
    LogTagProvider {
    override val TAG = "InAppNotificationStorage"
    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val timerTask = TimerTask(
        delayDuration = TIMER_DELAY,
        coroutineScope = coroutineScope,
        block = { invalidate() }
    )

    private val pendingNotification = Stack<InAppNotification>()
    private var listener: InAppNotificationListener? = null
    private var nextNotificationTime = 0L

    override fun subscribe(listener: InAppNotificationListener) {
        info { "#subscribe. Current listener: ${this.listener}, updated: $listener" }
        if (this.listener != null) {
            unsubscribe()
        }
        this.listener = listener
        timerTask.start()
        invalidate()
    }

    override fun unsubscribe() {
        info { "#unsubscribe. Current listener: ${this.listener}" }
        this.listener = null
        timerTask.shutdown()
    }

    @Synchronized
    override fun addNotification(notification: InAppNotification) {
        pendingNotification.push(notification)
        invalidate()
    }

    @Synchronized
    private fun invalidate() {
        val notificationListener = listener ?: return

        val currentTime = System.currentTimeMillis()
        if (currentTime < nextNotificationTime) {
            return
        }

        if (pendingNotification.empty()) {
            return
        }

        val notificationToShown = pendingNotification.pop()
        runBlockingWithLog {
            notificationListener.onNewNotification(notificationToShown)
        }
        nextNotificationTime = System.currentTimeMillis() + notificationToShown.durationMs
    }
}
