package com.flipperdevices.ifrmvp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pages can contain multiple [PageLayout] if we can't fit all buttons on the same page
 */
@Serializable
class PagesLayout(
    @SerialName("pages")
    val pages: List<PageLayout>
)
