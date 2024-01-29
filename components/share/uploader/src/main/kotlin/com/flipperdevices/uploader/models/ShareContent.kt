package com.flipperdevices.uploader.models

import com.flipperdevices.bridge.dao.api.model.FlipperKey

data class ShareContent(
    val link: String?,
    val flipperKey: FlipperKey
)
