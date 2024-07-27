package com.flipperdevices.ifrmvp.backend.model

import com.flipperdevices.ifrmvp.model.PageLayout
import com.flipperdevices.ifrmvp.model.PagesLayout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Copy of [PagesLayout] from core-model module
 * @see com.flipperdevices.ifrmvp.model.PagesLayout
 *
 * Workaround for https://github.com/Foso/Ktorfit/issues/594
 */
@Serializable
class PagesLayoutBackendModel(
    @SerialName("pages")
    val pages: List<PageLayout>
)

fun PagesLayoutBackendModel.toPagesLayout() = PagesLayout(pages)
