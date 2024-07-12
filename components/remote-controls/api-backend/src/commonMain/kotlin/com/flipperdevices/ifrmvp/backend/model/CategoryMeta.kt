package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CategoryMeta(
    @SerialName("icn_png_base64")
    val iconPngBase64: String,
    @SerialName("icn_svg_base64")
    val iconSvgBase64: String,
    @SerialName("manifest_content")
    val manifest: CategoryManifest
)
