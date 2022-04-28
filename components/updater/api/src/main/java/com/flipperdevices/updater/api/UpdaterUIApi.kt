package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.flipperdevices.updater.model.VersionFiles

interface UpdaterUIApi {
    @Composable
    fun getUpdateCardApi(): UpdateCardApi

    @Composable
    fun isUpdaterAvailable(): State<Boolean>

    /**
     * If silent is true, we don't show confirm dialog
     */
    fun openUpdateScreen(
        silent: Boolean = false,
        versionFiles: VersionFiles? = null
    )
}
