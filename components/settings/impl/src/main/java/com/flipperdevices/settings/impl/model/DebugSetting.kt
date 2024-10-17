package com.flipperdevices.settings.impl.model

sealed interface DebugSettingSwitch {
    data object IgnoreSupportedVersion : DebugSettingSwitch
    data object IgnoreUpdaterVersion : DebugSettingSwitch
    data object SkipProvisioning : DebugSettingSwitch
    data object SkipAutoSync : DebugSettingSwitch
    data object FapHubDev : DebugSettingSwitch
    data object SelfUpdaterDebug : DebugSettingSwitch
}

sealed interface DebugSettingAction {
    data object StressTest : DebugSettingAction
    data object StartSynchronization : DebugSettingAction
    data object RestartRPC : DebugSettingAction
    data object InstallAllFap : DebugSettingAction
    data object BrokeBytes : DebugSettingAction
}
