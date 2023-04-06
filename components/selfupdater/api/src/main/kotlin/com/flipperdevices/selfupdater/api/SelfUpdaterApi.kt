package com.flipperdevices.selfupdater.api

import android.app.Activity

interface SelfUpdaterApi {
    suspend fun startCheckUpdateAsync(activity: Activity)

    fun getInstallSourceName(): String
}
