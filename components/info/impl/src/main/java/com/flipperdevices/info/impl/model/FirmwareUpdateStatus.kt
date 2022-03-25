package com.flipperdevices.info.impl.model

sealed class FirmwareUpdateStatus {
    object UpToDate : FirmwareUpdateStatus()
    object Unsupported : FirmwareUpdateStatus()
}
