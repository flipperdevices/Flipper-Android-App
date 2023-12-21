package com.flipperdevices.bottombar.impl.model

import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.SelectedTab
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

/**
 *
 */
@Serializable
sealed interface BottomBarNavigationConfig {
    val uniqueId: Int
    val protobufRepresentation: SelectedTab
    val enum: BottomBarTabEnum

    @Serializable
    data class Device(override val uniqueId: Int = 0) : BottomBarNavigationConfig {
        override val protobufRepresentation = SelectedTab.DEVICE
        override val enum = BottomBarTabEnum.DEVICE
    }

    @Serializable
    data class Archive(override val uniqueId: Int = 0) : BottomBarNavigationConfig {
        override val protobufRepresentation = SelectedTab.ARCHIVE
        override val enum = BottomBarTabEnum.ARCHIVE
    }

    @Serializable
    data class Hub(override val uniqueId: Int = 0) : BottomBarNavigationConfig {
        override val protobufRepresentation = SelectedTab.HUB
        override val enum = BottomBarTabEnum.HUB
    }

    companion object {
        fun getInitialSync(
            dataStore: DataStore<Settings>
        ): BottomBarNavigationConfig = runBlocking {
            when (dataStore.data.first().selectedTab) {
                null,
                SelectedTab.DEVICE,
                SelectedTab.UNRECOGNIZED -> Device()

                SelectedTab.ARCHIVE -> Archive()
                SelectedTab.HUB -> Hub()
            }
        }
    }
}

fun BottomBarTabEnum.toConfig(uniqueId: Int = 0) = when (this) {
    BottomBarTabEnum.DEVICE -> BottomBarNavigationConfig.Device(uniqueId)
    BottomBarTabEnum.ARCHIVE -> BottomBarNavigationConfig.Archive(uniqueId)
    BottomBarTabEnum.HUB -> BottomBarNavigationConfig.Hub(uniqueId)
}
