package com.flipperdevices.archive.impl.model

import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
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
    data class InArchiveRemoteControl(val keyPath: FlipperKeyPath) : ArchiveNavigationConfig()

    @Serializable
    data object OpenSearch : ArchiveNavigationConfig()
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

        is Deeplink.BottomBar.ArchiveTab.ArchiveCategory.OpenSavedRemoteControl -> {
            val fileType = category
            if (fileType != null) {
                stack.add(
                    ArchiveNavigationConfig.InArchiveRemoteControl(keyPath)
                )
            }
        }

        null -> {}
    }
    return stack
}
