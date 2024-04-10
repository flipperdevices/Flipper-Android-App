package com.flipperdevices.faphub.screenshotspreview.api.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

@Serializable
data class ScreenshotsPreviewParam(
    val title: String,
    val screenshots: ImmutableList<String>,
    val selected: Int,
)
