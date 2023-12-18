package com.flipperdevices.hub.impl.model

import kotlinx.serialization.Serializable

@Serializable
sealed class HubNavigationConfig {
    @Serializable
    data object Main : HubNavigationConfig()

    @Serializable
    data object NfcAttack : HubNavigationConfig()

    @Serializable
    data object FapHub : HubNavigationConfig()
}
