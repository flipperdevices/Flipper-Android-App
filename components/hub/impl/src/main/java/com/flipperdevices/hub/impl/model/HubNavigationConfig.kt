package com.flipperdevices.hub.impl.model

import com.flipperdevices.deeplink.model.Deeplink
import kotlinx.serialization.Serializable

@Serializable
sealed class HubNavigationConfig {
    @Serializable
    data object Main : HubNavigationConfig()

    @Serializable
    data object NfcAttack : HubNavigationConfig()

    @Serializable
    data class FapHub(val deeplink: Deeplink.BottomBar.HubTab.FapHub?) : HubNavigationConfig()
}

fun Deeplink.BottomBar.HubTab?.toConfigStack(): List<HubNavigationConfig> {
    val stack = mutableListOf<HubNavigationConfig>(HubNavigationConfig.Main)
    when (this) {
        is Deeplink.BottomBar.HubTab.FapHub -> stack.add(HubNavigationConfig.FapHub(this))
        Deeplink.BottomBar.HubTab.OpenMfKey -> stack.add(HubNavigationConfig.NfcAttack)
        null -> {}
    }
    return stack
}
