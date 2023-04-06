package com.flipperdevices.selfupdater.api

import android.app.Activity

interface SelfUpdaterApi {
    fun startCheckUpdateAsync(activity: Activity)

    fun getInstallSourceName(): String
}
