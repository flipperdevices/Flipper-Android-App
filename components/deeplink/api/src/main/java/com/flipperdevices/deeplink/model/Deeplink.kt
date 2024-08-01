package com.flipperdevices.deeplink.model

import androidx.compose.runtime.Immutable
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed interface Deeplink {

    @Serializable
    sealed interface RootLevel : Deeplink {
        @Serializable
        sealed interface SaveKey : RootLevel {
            @Serializable
            data class ExternalContent(
                val content: DeeplinkContent? = null
            ) : SaveKey

            @Serializable
            data class FlipperKey(
                val path: FlipperFilePath,
                val content: DeeplinkContent? = null
            ) : SaveKey
        }

        @Serializable
        data class WidgetOptions(
            val appWidgetId: Int
        ) : RootLevel
    }

    @Serializable
    sealed interface BottomBar : Deeplink {
        @Serializable
        data class OpenTab(val bottomTab: DeeplinkBottomBarTab) : BottomBar

        @Serializable
        sealed interface DeviceTab : BottomBar {
            @Serializable
            data class WebUpdate(
                val url: String,
                val name: String,
            ) : DeviceTab

            @Serializable
            data object OpenUpdate : DeviceTab
        }

        @Serializable
        sealed interface ArchiveTab : BottomBar {
            @Serializable
            sealed interface ArchiveCategory : ArchiveTab {
                val category: FlipperKeyType?

                @Serializable
                data class OpenKey(
                    val keyPath: FlipperKeyPath
                ) : ArchiveCategory {
                    override val category = keyPath.path.keyType
                }
            }
        }

        @Serializable
        sealed interface AppsTab : BottomBar {
            @Serializable
            data class Fap(
                val appId: String,
            ) : AppsTab

            @Serializable
            sealed class MainScreen : AppsTab {
                @Serializable
                data object InstalledTab : MainScreen()
            }
        }

        @Serializable
        sealed interface ToolsTab : BottomBar {

            @Serializable
            data object OpenMfKey : ToolsTab
        }
    }
}
