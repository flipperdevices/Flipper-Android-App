package com.flipperdevices.bottombar.impl.model

import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.SelectedTab
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.deeplink.model.Deeplink
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
    data class Hub(
        val deeplink: Deeplink.BottomBar.HubTab?
    ) : BottomBarTabConfig {
        override val enum: BottomBarTabEnum = BottomBarTabEnum.HUB
    }

    companion object {
        fun getInitialConfig(
            dataStore: DataStore<Settings>,
            deeplink: Deeplink.BottomBar?
        ): BottomBarTabConfig {
            if (deeplink != null) {
                return when (deeplink) {
                    is Deeplink.BottomBar.ArchiveTab -> Archive(deeplink)
                    is Deeplink.BottomBar.DeviceTab -> Device(deeplink)
                    is Deeplink.BottomBar.HubTab -> Hub(deeplink)
                    is Deeplink.BottomBar.OpenTab -> deeplink.bottomTab.toBottomBarTabEnum()
                        .toConfig()
                }
            }
            return runBlocking {
                when (dataStore.data.first().selectedTab) {
                    null,
                    SelectedTab.DEVICE,
                    SelectedTab.UNRECOGNIZED -> Device(null)

                    SelectedTab.ARCHIVE -> Archive(null)
                    SelectedTab.HUB -> Hub(null)
                }
            }
        }
    }
}

fun BottomBarTabEnum.toConfig(): BottomBarTabConfig {
    return when (this) {
        BottomBarTabEnum.DEVICE -> BottomBarTabConfig.Device(null)
        BottomBarTabEnum.ARCHIVE -> BottomBarTabConfig.Archive(null)
        BottomBarTabEnum.HUB -> BottomBarTabConfig.Hub(null)
    }
}
