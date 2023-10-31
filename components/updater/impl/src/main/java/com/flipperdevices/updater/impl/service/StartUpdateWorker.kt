package com.flipperdevices.updater.impl.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.updater.impl.di.UpdaterComponent

class StartUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG: String = "StartUpdateWorker"

    init {
        ComponentHolder.component<UpdaterComponent>().inject(this)
    }

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

    override suspend fun getForegroundInfo() = super.getForegroundInfo()
}
