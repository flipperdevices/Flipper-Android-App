package com.flipperdevices.selfupdater.api

interface SelfUpdaterApi {
    fun startCheckUpdateAsync()

    fun getInstallSourceName(): String
}
