package com.flipperdevices.deeplink.model

import androidx.compose.runtime.Immutable
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed class Deeplink {

    @Serializable
    sealed class RootLevel : Deeplink() {
        @Serializable
        sealed class SaveKey : RootLevel() {
            @Serializable
            data class ExternalContent(
                val content: DeeplinkContent? = null
            ) : SaveKey()

            @Serializable
            data class FlipperKey(
                val path: FlipperFilePath,
                val content: DeeplinkContent? = null
            ) : SaveKey()
        }

        @Serializable
        data class WidgetOptions(
            val appWidgetId: Int
        ) : RootLevel()
    }

    @Serializable
    sealed class BottomBar : Deeplink() {
        @Serializable
        data class OpenTab(val bottomTab: DeeplinkBottomBarTab) : BottomBar()

        @Serializable
        sealed class DeviceTab : BottomBar() {
            @Serializable
            data class WebUpdate(
                val url: String,
                val name: String,
            ) : DeviceTab()

            @Serializable
            data object OpenUpdate : DeviceTab()
        }

        @Serializable
        sealed class ArchiveTab : BottomBar() {
            @Serializable
            data class OpenKey(
                val keyPath: FlipperKeyPath
            ) : ArchiveTab()
        }

        @Serializable
        sealed class HubTab : BottomBar() {
            sealed class FapHub : HubTab() {
                @Serializable
                data class Fap(
                    val appId: String,
                ) : FapHub()
            }

            @Serializable
            data object OpenMfKey : HubTab()
        }
    }
}
