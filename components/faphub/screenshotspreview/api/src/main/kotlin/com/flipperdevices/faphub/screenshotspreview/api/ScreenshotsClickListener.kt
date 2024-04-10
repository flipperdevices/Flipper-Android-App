package com.flipperdevices.faphub.screenshotspreview.api

import com.flipperdevices.faphub.screenshotspreview.api.model.ScreenshotsPreviewParam

fun interface ScreenshotsClickListener {
    fun onScreenshotClicked(param: ScreenshotsPreviewParam)
}
