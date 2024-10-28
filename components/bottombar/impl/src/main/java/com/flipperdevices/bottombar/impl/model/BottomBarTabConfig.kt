package com.flipperdevices.bottombar.impl.model

import com.flipperdevices.deeplink.model.Deeplink
import kotlinx.serialization.Serializable

@Serializable
sealed interface BottomBarTabConfig {
    val enum: BottomBarTabEnum

    @Serializable
    data class Device(
        val deeplink: Deeplink.BottomBar.DeviceTab?
    ) : BottomBarTabConfig {
        override val enum: BottomBarTabEnum = BottomBarTabEnum.DEVICE
    }

    @Serializable
    data class Archive(
        val deeplink: Deeplink.BottomBar.ArchiveTab?
    ) : BottomBarTabConfig {
        override val enum: BottomBarTabEnum = BottomBarTabEnum.ARCHIVE
    }

    @Serializable
    data class Apps(
        val deeplink: Deeplink.BottomBar.AppsTab?
    ) : BottomBarTabConfig {
        override val enum: BottomBarTabEnum = BottomBarTabEnum.APPS
    }

    @Serializable
    data class Tools(
        val deeplink: Deeplink.BottomBar.ToolsTab?
    ) : BottomBarTabConfig {
        override val enum: BottomBarTabEnum = BottomBarTabEnum.TOOLS
    }

    companion object {
        fun getInitialConfig(
            getSavedTab: () -> BottomBarTabConfig,
            deeplink: Deeplink.BottomBar?
        ): BottomBarTabConfig {
            if (deeplink != null) {
                return when (deeplink) {
                    is Deeplink.BottomBar.ArchiveTab -> Archive(deeplink)
                    is Deeplink.BottomBar.DeviceTab -> Device(deeplink)
                    is Deeplink.BottomBar.ToolsTab -> Tools(deeplink)
                    is Deeplink.BottomBar.AppsTab -> Apps(deeplink)
                    is Deeplink.BottomBar.OpenTab -> deeplink.bottomTab.toBottomBarTabEnum()
                        .toConfig()
                }
            }
            return getSavedTab.invoke()
        }
    }
}

fun BottomBarTabEnum.toConfig(): BottomBarTabConfig {
    return when (this) {
        BottomBarTabEnum.DEVICE -> BottomBarTabConfig.Device(null)
        BottomBarTabEnum.ARCHIVE -> BottomBarTabConfig.Archive(null)
        BottomBarTabEnum.APPS -> BottomBarTabConfig.Apps(null)
        BottomBarTabEnum.TOOLS -> BottomBarTabConfig.Tools(null)
    }
}
