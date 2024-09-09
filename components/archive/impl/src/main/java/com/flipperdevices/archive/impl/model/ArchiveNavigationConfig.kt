package com.flipperdevices.archive.impl.model

import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.deeplink.model.Deeplink
import kotlinx.serialization.Serializable

@Serializable
sealed class ArchiveNavigationConfig {
    @Serializable
    data object ArchiveObject : ArchiveNavigationConfig()

    @Serializable
    data class OpenCategory(
        val categoryType: CategoryType,
        val deeplink: Deeplink.BottomBar.ArchiveTab.ArchiveCategory?
    ) : ArchiveNavigationConfig()

    @Serializable
    data object OpenSearch : ArchiveNavigationConfig()

    @Serializable
    data object RemoteControls : ArchiveNavigationConfig()
}

fun Deeplink.BottomBar.ArchiveTab?.toArchiveNavigationStack(): List<ArchiveNavigationConfig> {
    val stack = mutableListOf<ArchiveNavigationConfig>(ArchiveNavigationConfig.ArchiveObject)
    when (this) {
        is Deeplink.BottomBar.ArchiveTab.ArchiveCategory.OpenKey -> {
            val fileType = category
            if (fileType != null) {
                stack.add(
                    ArchiveNavigationConfig.OpenCategory(
                        CategoryType.ByFileType(fileType),
                        this
                    )
                )
            }
        }

        null -> {
        }

        Deeplink.BottomBar.ArchiveTab.RemoteControls -> {
            stack.add(ArchiveNavigationConfig.RemoteControls)
        }
    }
    return stack
}
