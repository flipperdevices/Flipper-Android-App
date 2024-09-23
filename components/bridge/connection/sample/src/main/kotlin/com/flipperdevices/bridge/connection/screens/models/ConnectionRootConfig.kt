package com.flipperdevices.bridge.connection.screens.models

import kotlinx.serialization.Serializable

@Serializable
sealed class ConnectionRootConfig {
    @Serializable
    data object Search : ConnectionRootConfig()

    @Serializable
    data object NoPermission : ConnectionRootConfig()

    @Serializable
    data object FileManager : ConnectionRootConfig()

    @Serializable
    data object Device : ConnectionRootConfig()
}
