package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.UpdateRequest

interface UpdaterUIApi {
    /**
     * If silent is true, we don't show confirm dialog
     */
    fun openUpdateScreen(
        silent: Boolean = false,
        updateRequest: UpdateRequest? = null
    )
}
