package com.flipperdevices.widget.impl.broadcast

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.flipperdevices.widget.impl.tasks.EXTRA_KEY_WIDGET_ID
import com.flipperdevices.widget.impl.tasks.StopEmulateWorker
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsWorker

private const val WORK_CHAIN_STOP_NAME = "stop_emulating"
private val QUOTA_POLICY = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST

object StopChainBuilder {
    fun getStopChain(
        context: Context,
        widgetId: Int
    ) = WorkManager.getInstance(context)
        .beginUniqueWork(
            WORK_CHAIN_STOP_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<InvalidateWidgetsWorker>()
                .setExpedited(QUOTA_POLICY)
                .build()
        )
        .then(
            OneTimeWorkRequestBuilder<StopEmulateWorker>().setInputData(
                Data.Builder()
                    .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                    .build()
            ).setExpedited(QUOTA_POLICY)
                .build()
        )
        .then(
            OneTimeWorkRequestBuilder<InvalidateWidgetsWorker>()
                .setExpedited(QUOTA_POLICY)
                .build()
        )
}