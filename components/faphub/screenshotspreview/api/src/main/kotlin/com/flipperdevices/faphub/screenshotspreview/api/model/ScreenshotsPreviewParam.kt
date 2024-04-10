package com.flipperdevices.faphub.screenshotspreview.api.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

@Serializable
/**
 * @param title name of catalog item
 * @param screenshotsUrls list of web-urls where screenshot available
 * @param selected index of selected [screenshotsUrls] item
 */
data class ScreenshotsPreviewParam(
    val title: String,
    val screenshotsUrls: ImmutableList<String>,
    val selected: Int,
)
