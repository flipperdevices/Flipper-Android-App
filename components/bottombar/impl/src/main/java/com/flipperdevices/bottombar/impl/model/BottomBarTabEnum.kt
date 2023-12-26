package com.flipperdevices.bottombar.impl.model

import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.SelectedTab
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
enum class BottomBarTabEnum(val protobufRepresentation: SelectedTab) {
    DEVICE(SelectedTab.DEVICE),
    ARCHIVE(SelectedTab.ARCHIVE),
    HUB(SelectedTab.HUB);

    companion object {

        fun getInitialSync(
            dataStore: DataStore<Settings>
        ): BottomBarTabEnum = runBlocking {
            when (dataStore.data.first().selectedTab) {
                null,
                SelectedTab.DEVICE,
                SelectedTab.UNRECOGNIZED -> DEVICE

                SelectedTab.ARCHIVE -> ARCHIVE
                SelectedTab.HUB -> HUB
            }
        }
    }
}
