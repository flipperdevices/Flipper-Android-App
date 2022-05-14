package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import com.flipperdevices.updater.model.VersionFiles

interface UpdaterUIApi {
    @Composable
    fun getUpdateCardApi(): UpdateCardApi

    /**
     * If silent is true, we don't show confirm dialog
     */
    fun openUpdateScreen(
        silent: Boolean = false,
        versionFiles: VersionFiles? = null
    )
}
