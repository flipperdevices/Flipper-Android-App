package com.flipperdevices.nfc.attack.impl.model

import kotlinx.serialization.Serializable

@Serializable
sealed class NFCAttackNavigationConfig {
    @Serializable
    data object NFCAttack : NFCAttackNavigationConfig()

    @Serializable
    data object MfKey32 : NFCAttackNavigationConfig()
}
