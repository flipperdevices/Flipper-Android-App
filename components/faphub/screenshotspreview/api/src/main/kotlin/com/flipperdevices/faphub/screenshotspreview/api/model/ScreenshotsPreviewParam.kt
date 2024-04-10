package com.flipperdevices.faphub.screenshotspreview.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ScreenshotsPreviewParam(
    val title: String,
    val screenshots: List<String>,
    val selected: Int,
)
