package com.flipperdevices.updater.card.model

import android.content.Context
import android.net.Uri
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateRequest

sealed class UpdatePending {
    data class URI(
        val uri: Uri,
        val context: Context,
        val currentVersion: FirmwareVersion
    ) : UpdatePending()
    data class Request(val updateRequest: UpdateRequest) : UpdatePending()
}
