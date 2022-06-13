package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.VersionFiles

interface UpdaterUIApi {
    /**
     * If silent is true, we don't show confirm dialog
     */
    fun openUpdateScreen(
        silent: Boolean = false,
        versionFiles: VersionFiles? = null
    )
}
