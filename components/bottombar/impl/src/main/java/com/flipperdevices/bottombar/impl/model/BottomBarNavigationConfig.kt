package com.flipperdevices.bottombar.impl.model

import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.SelectedTab
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
sealed interface BottomBarNavigationConfig {
    val protobufRepresentation: SelectedTab

    @Serializable
    data object Device : BottomBarNavigationConfig {
        override val protobufRepresentation = SelectedTab.DEVICE
    }

    @Serializable
    data object Archive : BottomBarNavigationConfig {
        override val protobufRepresentation = SelectedTab.ARCHIVE
    }

    @Serializable
    data object Hub : BottomBarNavigationConfig {
        override val protobufRepresentation = SelectedTab.HUB
    }

    companion object {
        val entries = listOf(Device, Archive, Hub)

        fun getInitialSync(
            dataStore: DataStore<Settings>
        ): BottomBarNavigationConfig = runBlocking {
            when (dataStore.data.first().selectedTab) {
                null,
                SelectedTab.DEVICE,
                SelectedTab.UNRECOGNIZED -> Device

                SelectedTab.ARCHIVE -> Archive
                SelectedTab.HUB -> Hub
            }
        }
    }
}
