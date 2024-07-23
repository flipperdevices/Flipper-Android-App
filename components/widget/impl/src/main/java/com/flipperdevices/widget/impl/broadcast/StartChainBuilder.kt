package com.flipperdevices.widget.impl.broadcast

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.widget.impl.tasks.EXTRA_KEY_FILE_PATH
import com.flipperdevices.widget.impl.tasks.EXTRA_KEY_WIDGET_ID
import com.flipperdevices.widget.impl.tasks.StartEmulateWorker
import com.flipperdevices.widget.impl.tasks.StopEmulateWorker
import com.flipperdevices.widget.impl.tasks.WaitForEmulateEndWorker
import com.flipperdevices.widget.impl.tasks.WaitingForFlipperConnectWorker
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsWorker

private const val WORK_CHAIN_START_NAME = "start_emulating"
private val QUOTA_POLICY = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST

object StartChainBuilder {
    fun getStartChain(
        context: Context,
        filePath: FlipperFilePath,
        widgetId: Int,
        oneTimeEmulation: Boolean
    ): WorkContinuation {
        var chain = WorkManager.getInstance(context).beginUniqueWork(
            WORK_CHAIN_START_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<InvalidateWidgetsWorker>()
                .setExpedited(QUOTA_POLICY)
                .build()
        ).applyStartChain(filePath, widgetId)

        if (oneTimeEmulation) {
            chain = chain.applyStopChain(widgetId)
        } else {
            chain = chain.applyWaitToStopChain(widgetId)
        }

        return chain
    }

    private fun WorkContinuation.applyStartChain(
        filePath: FlipperFilePath,
        widgetId: Int
    ) = then(
        OneTimeWorkRequestBuilder<WaitingForFlipperConnectWorker>()
            .setInputData(
                Data.Builder()
                    .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                    .build()
            )
            .setExpedited(QUOTA_POLICY)
            .build()
    ).then(
        OneTimeWorkRequestBuilder<StartEmulateWorker>()
            .setInputData(
                Data.Builder()
                    .putString(EXTRA_KEY_FILE_PATH, filePath.pathToKey)
                    .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                    .build()
            )
            .setExpedited(QUOTA_POLICY)
            .build()
    ).then(
        OneTimeWorkRequestBuilder<InvalidateWidgetsWorker>()
            .setExpedited(QUOTA_POLICY)
            .build()
    )

    private fun WorkContinuation.applyWaitToStopChain(
        widgetId: Int
    ) = then(
        OneTimeWorkRequestBuilder<WaitForEmulateEndWorker>()
            .setExpedited(QUOTA_POLICY)
            .build()
    ).then(
        OneTimeWorkRequestBuilder<StopEmulateWorker>().setInputData(
            Data.Builder()
                .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                .build()
        ).setExpedited(QUOTA_POLICY)
            .build()
    ).then(
        OneTimeWorkRequestBuilder<InvalidateWidgetsWorker>()
            .setExpedited(QUOTA_POLICY)
            .build()
    )

    private fun WorkContinuation.applyStopChain(
        widgetId: Int
    ) = then(
        OneTimeWorkRequestBuilder<StopEmulateWorker>().setInputData(
            Data.Builder()
                .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                .build()
        ).setExpedited(QUOTA_POLICY)
            .build()
    ).then(
        OneTimeWorkRequestBuilder<InvalidateWidgetsWorker>()
            .setExpedited(QUOTA_POLICY)
            .build()
    )
}
