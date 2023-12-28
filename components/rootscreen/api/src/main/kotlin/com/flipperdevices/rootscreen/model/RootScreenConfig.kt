package com.flipperdevices.rootscreen.model

import com.flipperdevices.updater.model.UpdateRequest
import kotlinx.serialization.Serializable

@Serializable
sealed class RootScreenConfig {
    @Serializable
    data object FirstPair : RootScreenConfig()

    @Serializable
    data object BottomBar : RootScreenConfig()

    @Serializable
    data class UpdateScreen(val updateRequest: UpdateRequest?) : RootScreenConfig()

    @Serializable
    data object ScreenStreaming : RootScreenConfig()
}
