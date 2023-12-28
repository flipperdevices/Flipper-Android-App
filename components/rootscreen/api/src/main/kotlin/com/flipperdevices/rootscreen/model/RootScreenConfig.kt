package com.flipperdevices.rootscreen.model

import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.updater.model.UpdateRequest
import kotlinx.serialization.Serializable

@Serializable
sealed class RootScreenConfig {
    @Serializable
    data class FirstPair(val pendingDeeplink: Deeplink?) : RootScreenConfig()

    @Serializable
    data class BottomBar(val bottomBarDeeplink: Deeplink.BottomBar?) : RootScreenConfig()

    @Serializable
    data class UpdateScreen(val updateRequest: UpdateRequest?) : RootScreenConfig()

    @Serializable
    data object ScreenStreaming : RootScreenConfig()

    @Serializable
    data class WidgetOptions(val widgetId: Int) : RootScreenConfig()

    @Serializable
    data class SaveKey(val saveKeyDeeplink: Deeplink.RootLevel.SaveKey) : RootScreenConfig()
}
