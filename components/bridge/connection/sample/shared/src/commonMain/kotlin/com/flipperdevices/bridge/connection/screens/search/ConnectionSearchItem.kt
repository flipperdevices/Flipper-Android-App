package com.flipperdevices.bridge.connection.screens.search

import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel

data class ConnectionSearchItem(
    val address: String,
    val deviceModel: FDeviceBaseModel,
    val isAdded: Boolean
)
